package com.bit.jwtauthservice.service;

import com.bit.jwtauthservice.entity.User;
import com.bit.jwtauthservice.exceptions.usernotfound.UserNotFoundException;
import com.bit.jwtauthservice.repository.UserRepository;
import com.bit.jwtauthservice.service.service_impl.CustomUserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsServiceImpl customUserDetailsServiceImpl;

    @BeforeEach
    void setUp() {
        customUserDetailsServiceImpl = new CustomUserDetailsServiceImpl(userRepository);
    }

    @Test
    void userDetailsService_whenUserCodeExists_shouldReturnUserDetails() {
        User user = new User();
        user.setUserCode("userCode");

        given(userRepository.findByUserCode(user.getUserCode())).willReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsServiceImpl.userDetailsService().loadUserByUsername(user.getUserCode());

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(user.getUserCode());
    }

    @Test
    void userDetailsService_whenUserCodeDoesNotExist_shouldThrowUserNotFoundException() {
        String userCode = "userCode123";
        given(userRepository.findByUserCode(userCode)).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> customUserDetailsServiceImpl.userDetailsService().loadUserByUsername(userCode));
    }
}