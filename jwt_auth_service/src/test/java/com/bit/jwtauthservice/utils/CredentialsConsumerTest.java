package com.bit.jwtauthservice.utils;

import com.bit.jwtauthservice.dto.kafka.*;
import com.bit.jwtauthservice.entity.Role;
import com.bit.jwtauthservice.entity.User;
import com.bit.jwtauthservice.exceptions.usernotfound.UserNotFoundException;
import com.bit.jwtauthservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CredentialsConsumerTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    CredentialsConsumer credentialsConsumer;

    private Set<Role> roles;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        roles = new HashSet<>();
        roles.add(new Role("ROLE_USER"));
    }

    @Test
    void testListen_UserCredentialsDTO() {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setId(1L);
        userCredentialsDTO.setEmail("test@example.com");
        userCredentialsDTO.setUserCode("user123");
        userCredentialsDTO.setPassword("password");
        userCredentialsDTO.setRoles(roles);
        userCredentialsDTO.setDeleted(false);

        credentialsConsumer.listen(userCredentialsDTO);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testListen_UserSafeDeletionDTO() {
        UserSafeDeletionDTO userSafeDeletionDTO = new UserSafeDeletionDTO();
        userSafeDeletionDTO.setId(1L);
        userSafeDeletionDTO.setDeleted(true);

        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));

        assertDoesNotThrow(() -> credentialsConsumer.listen(userSafeDeletionDTO));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testListen_UserSafeDeletionDTO_UserNotFoundException() {
        UserSafeDeletionDTO userSafeDeletionDTO = new UserSafeDeletionDTO();
        userSafeDeletionDTO.setId(1L);
        userSafeDeletionDTO.setDeleted(true);

        when(userRepository.findById(any())).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> credentialsConsumer.listen(userSafeDeletionDTO));
    }

    @Test
    void testListen_UserUpdateDTO() {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setId(1L);
        userUpdateDTO.setEmail("test@example.com");
        userUpdateDTO.setUserCode("user123");
        userUpdateDTO.setRoles(roles);

        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));

        assertDoesNotThrow(() -> credentialsConsumer.listen(userUpdateDTO));

        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void testListen_UserUpdateDTO_UserNotFoundException() {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setId(1L);
        userUpdateDTO.setEmail("test@example.com");
        userUpdateDTO.setUserCode("user123");
        userUpdateDTO.setRoles(roles);

        when(userRepository.findById(any())).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> credentialsConsumer.listen(userUpdateDTO));
    }

    @Test
    void testListen_UserReactivateDTO() {
        UserReactivateDTO userReactivateDTO = new UserReactivateDTO();
        userReactivateDTO.setId(1L);
        userReactivateDTO.setPassword("newPassword");
        userReactivateDTO.setDeleted(false);

        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));

        assertDoesNotThrow(() -> credentialsConsumer.listen(userReactivateDTO));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testListen_UserReactivateDTO_UserNotFoundException() {
        UserReactivateDTO userReactivateDTO = new UserReactivateDTO();
        userReactivateDTO.setId(1L);
        userReactivateDTO.setPassword("newPassword");
        userReactivateDTO.setDeleted(false);

        when(userRepository.findById(any())).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> credentialsConsumer.listen(userReactivateDTO));
    }
}