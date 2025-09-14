package com.example.ex3.config.security.filters;

import com.example.ex3.config.security.authentication.CustomAuthentication;
import com.example.ex3.config.security.managers.CustomAuthenticationManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final CustomAuthenticationManager customAuthenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. create authentication object not yet authorised
        String key = String.valueOf(request.getHeader("key")); // Get the key from the request header
        CustomAuthentication ca = new CustomAuthentication(false, key);

        // 2. delegate to authentication manager
        var a = customAuthenticationManager.authenticate(ca);

        // 3. get back authentication from manager
        if (a.isAuthenticated()) {

            // 4, if object is authenticated, then send to next filter in chain
            SecurityContextHolder.getContext().setAuthentication(a); // store auth object so later we can get user details
            filterChain.doFilter(request, response); // only when authentication is successful

        }

    }

}
