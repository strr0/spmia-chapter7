package com.strr.authentication.security;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT令牌增强
 */
public class JWTTokenEnhancer implements TokenEnhancer {
    private String getOrgId(String userName) {
        return String.format("%s-Organization", userName);
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        // 附加信息
        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("organizationId", getOrgId(oAuth2Authentication.getName()));
        ((DefaultOAuth2AccessToken)oAuth2AccessToken).setAdditionalInformation(additionalInfo);
        return oAuth2AccessToken;
    }
}
