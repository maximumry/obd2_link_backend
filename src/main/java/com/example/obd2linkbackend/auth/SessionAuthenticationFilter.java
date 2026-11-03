package com.example.obd2linkbackend.auth;

import java.io.IOException;

import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.http.HttpHeaders;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.obd2linkbackend.user.mapper.UserMapper;
import com.example.obd2linkbackend.user.model.entity.SessionEntity;
import com.example.obd2linkbackend.user.model.entity.UserEntity;
import com.example.obd2linkbackend.user.service.SessionService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor 
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private final SessionService sessionService;
    private final UserMapper userMapper;

    @Override 
    protected void doFilterInternal(@NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws ServletException, IOException{

        String token = resolve(request);
        if(token != null){
            SessionEntity entity = sessionService.findValidSession(token);
            if(entity != null){
                UserEntity user = userMapper.getUser(entity.getUserId());

                if(user != null){
                    CustomUserDetails principal = new CustomUserDetails(user);
                    var authentication = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                    );
                    var context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolve(HttpServletRequest request){
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
    
}
