package com.bit.sharedFilter.utils;

import com.bit.sharedFilter.dto.UserDetailsDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserDetailsAdapter implements UserDetails {
    private final UserDetailsDTO userDetailsDTO;
    public UserDetailsAdapter(UserDetailsDTO userDetailsDTO) {
        this.userDetailsDTO = userDetailsDTO;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDetailsDTO.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return userDetailsDTO.getPassword();
    }

    @Override
    public String getUsername() {
        return userDetailsDTO.getUserCode();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
