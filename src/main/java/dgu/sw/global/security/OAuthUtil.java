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

    public String requestAccessToken(OAuthProvider provider, String code) {

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
        if (provider != OAuthProvider.KAKAO) {
            throw new OAuthException(ErrorStatus.OAUTH_UNSUPPORTED_PROVIDER);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(OAuthConstants.KAKAO_PROFILE_URL, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            System.out.println("카카오 프로필 응답 오류: " + response.getBody());
            throw new OAuthException(ErrorStatus.OAUTH_REQUEST_FAILED);
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            JsonNode kakaoAccount = jsonNode.get("kakao_account");

            if (kakaoAccount == null || !kakaoAccount.has("email") || !kakaoAccount.has("profile")) {
                throw new OAuthException(ErrorStatus.OAUTH_JSON_PARSE_ERROR);
            }

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
}