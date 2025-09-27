package com.example.ex8.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(1) // for authorization server
    public SecurityFilterChain asSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class) // configure OAuth2 Authorization Server
            .oidc(withDefaults()); // enable OpenID Connect 1.0
        http.exceptionHandling(
            e -> e
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login") // redirect to login page if not authenticated
            )
        )
            .oauth2ResourceServer(rs -> rs.jwt(withDefaults()));
        return http.build();
    }

    @Bean
    @Order(2) // for application
    public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(withDefaults()) // enable form login
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated() // all requests require authentication
            );

        return http.build();
    }

    // Define a user for authentication
    @Bean
    public UserDetailsService userDetailsService() {
        var uds = new InMemoryUserDetailsManager();
        var user = User.withUsername("user")
            .password("{noop}password") // {noop} means no password encoding
            .roles("USER")
            .build();
        uds.createUser(user);
        return uds;
    }

    // Define client applications that can access the authorization server
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient rc = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("oidc-client")
            .clientSecret("{noop}secret") // hash in production
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("http://127.0.0.1:8080/login/oauth2/code/oidc-client")
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .scope("read_data")
            .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
            .build();
        return new InMemoryRegisteredClientRepository(rc);
    }

    // Configure issuer URL and other settings for the authorization server
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    // Configure how JWTs are generated and signed
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            var keyPair = keyPairGenerator.generateKeyPair();

            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            com.nimbusds.jose.jwk.RSAKey rsaKey = new com.nimbusds.jose.jwk.RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();

            JWKSet jwkSet = new JWKSet(rsaKey);
            return new ImmutableJWKSet<>(jwkSet);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate RSA key pair", e);
        }
    }
}
