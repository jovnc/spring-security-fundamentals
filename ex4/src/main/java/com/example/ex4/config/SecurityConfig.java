package com.example.ex4.config;

import com.example.ex4.config.filters.ApiKeyFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Value("${secret.key}")
    private String key;

    @Bean
    public SecurityFilterChain springSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.httpBasic() // 1. using http basic authentication
                .and().addFilterBefore(new ApiKeyFilter(key), BasicAuthenticationFilter.class) // 2. adding custom filter before BasicAuthenticationFilter
                .authorizeRequests().anyRequest().authenticated()
                .and().build();
    }
}
