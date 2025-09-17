package com.example.ex5.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // enable HTTP Basic authentication
            .httpBasic(Customizer.withDefaults())
            // endpoint level authorization
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/").hasAuthority("read") // GET requests to "/" need "read" authority
                .requestMatchers(HttpMethod.POST, "/").hasAuthority("write") // POST requests to "/" need "write" authority
                .requestMatchers(HttpMethod.POST, "/hello/**").hasRole("ADMIN") // POST requests to "/hello/**" need "ADMIN" role
                .anyRequest().authenticated() // All other requests require authentication
            )
            .csrf(AbstractHttpConfigurer::disable); // DO NOT USE IN PRODUCTION
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        // User 1 - only read permission
        manager.createUser(User
            .withUsername("user")
            .password("{noop}password")
            .authorities("read")
            .build());
        // User 2 - read and write permissions, plus ADMIN role
        manager.createUser(User
            .withUsername("user2")
            .password("{noop}password")
            // Cannot use .roles("ADMIN") here because we are also assigning authorities
            .authorities("read", "write", "ROLE_ADMIN") // Note: "ROLE_" prefix is required for roles
            .build());

        return manager;
    }
}
