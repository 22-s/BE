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
//            case APPLE -> requestAppleUserProfile(accessToken);
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
            String profileImage = kakaoAccount.get("profile").get("thumbnail_image_url").asText();

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

    public void logoutFromProvider(OAuthProvider provider) {
        if (provider == null) {
            System.out.println("❗ OAuthProvider가 null입니다. 로그아웃 생략");
            return;
        }
        switch (provider) {
            case KAKAO -> logoutFromKakao();
            case NAVER -> logoutFromNaver();
            case GOOGLE -> logoutFromGoogle();
            default -> throw new OAuthException(ErrorStatus.OAUTH_UNSUPPORTED_PROVIDER);
        }
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
}