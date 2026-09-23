package com.example.globelink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsManager userDetailsManager(){
        return new 
    }

    @Bean
    public UserDetailsService userDetailsService(){
        CustomUserDetails user = User
        .withUserUsename("example")
        .roles("USER")
        .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.formLogin(login -> login
            .permitAll())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/").permitAll()
                .anyRequest().authenticated()
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
