package com.example.obd2linkbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form
                .permitAll()
            );
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/register/*", "/auth/login/*").permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }
    
}
