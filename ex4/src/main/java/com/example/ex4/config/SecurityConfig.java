package com.example.ex4.config;

import com.example.ex4.config.filters.ApiKeyFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${secret.key}")
    private String key;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(withDefaults()) // 1. using http basic authentication
                .addFilterBefore(new ApiKeyFilter(key), BasicAuthenticationFilter.class) // 2. adding custom filter before BasicAuthenticationFilter
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .build();
    }
}
