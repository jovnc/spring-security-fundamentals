package com.example.ex4.config.providers;

import com.example.ex4.config.authentication.ApiKeyAuthentication;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@AllArgsConstructor
public class ApiKeyProvider implements AuthenticationProvider {

    private final String key;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ApiKeyAuthentication apiKeyAuthentication = (ApiKeyAuthentication) authentication;
        if (key.equals(apiKeyAuthentication.getApiKey())) {
            apiKeyAuthentication.setAuthenticated(true);
            return apiKeyAuthentication;
        }
        throw new BadCredentialsException("Invalid api key");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthentication.class.equals(authentication);
    }
}
