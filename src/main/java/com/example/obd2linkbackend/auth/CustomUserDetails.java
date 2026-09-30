package com.example.obd2linkbackend.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.obd2linkbackend.user.model.entity.UserEntity;

public class CustomUserDetails implements UserDetails {
    private final String email;
    private final String displayName;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(UserEntity user){
        this.email = user.getEmail();
        this.displayName = user.getDisplayName();
        this.authorities = java.util.List.of();
    }

    @Override 
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return authorities;
    }

    @Override 
    public String getUsername(){
        return email;
    }

    public String getName() {
        return displayName;
    }

    @Override 
    public String getPassword(){
        return null; //パスキー認証のためパスワードは設定しない
    }

    /**
     * アカウントが有効かどうかを返す
     */
    @Override 
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override 
    public boolean isAccountNonLocked(){
        return true;
    }

    @Override 
    public boolean isCredentialsNonExpired(){
        return true;
    }

    @Override 
    public boolean isEnabled(){
        return true;
    }
}
