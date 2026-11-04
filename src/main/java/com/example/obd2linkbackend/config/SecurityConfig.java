package com.example.obd2linkbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.obd2linkbackend.auth.SessionAuthenticationFilter;
import com.example.obd2linkbackend.user.mapper.UserMapper;
import com.example.obd2linkbackend.user.service.SessionService;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
            SessionService sessionService,
            UserMapper userMapper) throws Exception{

        var filter = new SessionAuthenticationFilter(sessionService, userMapper);

        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, ex) ->
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                )
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/register/*", "/auth/login/*"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                filter, UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
    
}
