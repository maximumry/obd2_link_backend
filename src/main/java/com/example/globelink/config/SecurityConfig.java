package com.example.globelink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            //  パスキー（webauthn）を有効化
            // → /webauthn/register エンドポイントが生成される
            // → ログインページに「パスキーでログイン」ボタンが追加される
            .webAuthn(webAuthn -> webAuthn
                .rpName("GlobeLink")
                .rpId("localhost")
                .allowedOrigins("http://localhost:8080")
            );
            // .authorizeHttpRequests(auth -> auth
            //     .requestMatchers("/login", "/register").permitAll()
            //     // .anyRequest().authenticated()
            // );
        
            return http.build();
    }
    
}
