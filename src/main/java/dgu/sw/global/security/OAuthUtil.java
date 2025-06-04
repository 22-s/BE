package dgu.sw.global.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dgu.sw.domain.auth.dto.AuthUserProfile;
import dgu.sw.global.exception.OAuthException;
import dgu.sw.global.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Component
@RequiredArgsConstructor
public class OAuthUtil {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${oauth.naver.client-id}")
    private String naverClientId;

    @Value("${oauth.naver.client-secret}")
    private String naverClientSecret;

    @Value("${oauth.apple.client-id}")
    private String appleClientId;

    @Value("${oauth.apple.key-id}")
    private String appleKeyId;

    @Value("${oauth.apple.team-id}")
    private String appleTeamId;

    @Value("${oauth.apple.private-key}")
    private String applePrivateKey; // `.p8` 파일을 문자열로 환경변수에 저장한 경우


    public String requestAccessToken(OAuthProvider provider, String code) {
        // 보통 SDK 방식에서는 호출되지 않음
        if (provider != OAuthProvider.KAKAO) {
            throw new OAuthException(ErrorStatus.OAUTH_UNSUPPORTED_PROVIDER);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(OAuthConstants.KAKAO_TOKEN_URL, request, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new OAuthException(ErrorStatus.OAUTH_REQUEST_FAILED);
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new OAuthException(ErrorStatus.OAUTH_JSON_PARSE_ERROR);
        }
    }

    public AuthUserProfile requestUserProfile(OAuthProvider provider, String accessToken) {
        return switch (provider) {
            case KAKAO -> requestKakaoUserProfile(accessToken);
            case NAVER -> requestNaverUserProfile(accessToken);
            case GOOGLE -> requestGoogleUserProfile(accessToken);
            case APPLE -> requestAppleUserProfile(accessToken);
            default -> throw new OAuthException(ErrorStatus.OAUTH_UNSUPPORTED_PROVIDER);
        };
    }

    private AuthUserProfile requestKakaoUserProfile(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                OAuthConstants.KAKAO_PROFILE_URL,
                HttpMethod.GET,
                request,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new OAuthException(ErrorStatus.OAUTH_REQUEST_FAILED);
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            JsonNode kakaoAccount = jsonNode.get("kakao_account");

            String email = kakaoAccount.get("email").asText();
            String nickname = kakaoAccount.get("profile").get("nickname").asText();
            String profileImageRaw = kakaoAccount.get("profile").get("thumbnail_image_url").asText();
            String profileImage = profileImageRaw.replace("http://", "https://");

            return AuthUserProfile.builder()
                    .provider(OAuthProvider.KAKAO)
                    .email(email)
                    .nickname(nickname)
                    .profileImage(profileImage)
                    .build();
        } catch (Exception e) {
            throw new OAuthException(ErrorStatus.OAUTH_JSON_PARSE_ERROR);
        }
    }

    private AuthUserProfile requestNaverUserProfile(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                OAuthConstants.NAVER_PROFILE_URL,
                HttpMethod.GET,
                request,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new OAuthException(ErrorStatus.OAUTH_REQUEST_FAILED);
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody()).get("response");
            String email = jsonNode.get("email").asText();
            String nickname = jsonNode.get("name").asText();
            String profileImage = jsonNode.get("profile_image").asText();

            return AuthUserProfile.builder()
                    .provider(OAuthProvider.NAVER)
                    .email(email)
                    .nickname(nickname)
                    .profileImage(profileImage)
                    .build();
        } catch (Exception e) {
            throw new OAuthException(ErrorStatus.OAUTH_JSON_PARSE_ERROR);
        }
    }

    private AuthUserProfile requestGoogleUserProfile(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                OAuthConstants.GOOGLE_PROFILE_URL,
                HttpMethod.GET,
                request,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new OAuthException(ErrorStatus.OAUTH_REQUEST_FAILED);
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String email = jsonNode.get("email").asText();
            String nickname = jsonNode.get("name").asText();
            String profileImage = jsonNode.get("picture").asText();

            return AuthUserProfile.builder()
                    .provider(OAuthProvider.GOOGLE)
                    .email(email)
                    .nickname(nickname)
                    .profileImage(profileImage)
                    .build();
        } catch (Exception e) {
            throw new OAuthException(ErrorStatus.OAUTH_JSON_PARSE_ERROR);
        }
    }

    private AuthUserProfile requestAppleUserProfile(String identityToken) {
        try {
            // Apple의 identityToken은 JWT 형식이며, 가운데 payload를 Base64로 디코딩하여 정보 추출
            String[] chunks = identityToken.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));

            JsonNode jsonNode = objectMapper.readTree(payload);

            String email = jsonNode.has("email") ? jsonNode.get("email").asText() : null;
            if (email == null) throw new OAuthException(ErrorStatus.OAUTH_MISSING_EMAIL);

            return AuthUserProfile.builder()
                    .provider(OAuthProvider.APPLE)
                    .email(email)
                    .nickname("신입사원") // 고정 닉네임
                    .profileImage(null)
                    .build();

        } catch (Exception e) {
            throw new OAuthException(ErrorStatus.OAUTH_JSON_PARSE_ERROR);
        }
    }

    public void logoutFromProvider(OAuthProvider provider) {
        if (provider == null) {
            System.out.println("❗ OAuthProvider가 null입니다. 로그아웃 생략");
            return;
        }
        switch (provider) {
            case KAKAO -> logoutFromKakao();
            case NAVER -> logoutFromNaver();
            case GOOGLE -> logoutFromGoogle();
            case APPLE -> logoutFromApple();
            default -> throw new OAuthException(ErrorStatus.OAUTH_UNSUPPORTED_PROVIDER);
        }
    }

    private String generateAppleClientSecret() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Algorithm algorithm = Algorithm.ECDSA256(null,
                (ECPrivateKey) KeyFactory.getInstance("EC")
                        .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(applePrivateKey))));

        return JWT.create()
                .withIssuer(appleTeamId)
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(Instant.now().plus(180, ChronoUnit.DAYS))) // 6개월 유효
                .withAudience("https://appleid.apple.com")
                .withSubject(appleClientId)
                .withKeyId(appleKeyId)
                .sign(algorithm);
    }


    private void logoutFromKakao() {
        // 실제 구현 시 사용자 accessToken 등을 활용하여 로그아웃 API 호출 가능
        System.out.println("👉 카카오 로그아웃 요청 완료 (추후 SDK 연동 필요)");
    }

    private void logoutFromNaver() {
        System.out.println("👉 네이버 로그아웃 요청 완료 (추후 SDK 연동 필요)");
    }

    private void logoutFromGoogle() {
        System.out.println("👉 구글 로그아웃 요청 완료 (추후 SDK 연동 필요)");
    }

    private void logoutFromApple() {
        System.out.println("👉 애플 로그아웃 요청 완료 (추후 SDK 연동 필요)");
    }

    public void withDrawApple(String refreshToken) {
        try {
            String clientSecret = generateAppleClientSecret();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", appleClientId);
            params.add("client_secret", clientSecret);
            params.add("token", refreshToken);
            params.add("token_type_hint", "refresh_token");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://appleid.apple.com/auth/revoke",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("✅ Apple 토큰 해지 완료");
            } else {
                System.out.println("❌ Apple 토큰 해지 실패: " + response.getBody());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new OAuthException(ErrorStatus.OAUTH_REQUEST_FAILED);
        }
    }

}