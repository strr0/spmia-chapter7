package com.strr.authentication.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

//@Configuration
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {
    private static final String CLIENT = "eagleeye";
    private static final String SECRET = "thisissecret";
    private static final String[] GRANT_TYPES = new String[] {"refresh_token", "password", "client_credentials"};
    private static final String[] SCOPES = new String[] {"webclient", "mobileclient"};

    private final AuthenticationManager authenticationManager;

    @Autowired
    public OAuth2Config(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(CLIENT)
                .secret(SECRET)
                .authorizedGrantTypes(GRANT_TYPES)
                .scopes(SCOPES);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager);
    }
}
