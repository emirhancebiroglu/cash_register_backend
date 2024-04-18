package com.bit.jwtauthservice.service;
import com.bit.jwtauthservice.entity.RefreshToken;
import com.bit.jwtauthservice.entity.User;
import com.bit.jwtauthservice.exceptions.expiredrefreshtoken.ExpiredRefreshTokenException;
import com.bit.jwtauthservice.repository.RefreshTokenRepository;
import com.bit.jwtauthservice.repository.UserRepository;
import com.bit.jwtauthservice.service.service_impl.RefreshTokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Test
    void createRefreshToken_WithValidUser_ShouldCreateRefreshToken() {
        User user = new User();
        user.setUserCode("sampleUserCode");

        when(userRepository.findByUserCode(user.getUserCode())).thenReturn(Optional.of(user));

        refreshTokenService.createRefreshToken(user);

        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void verifyExpiration_WithNonExpiredToken_ShouldReturnToken() {
        User user = new User();
        user.setUserCode("sampleUserCode");

        RefreshToken token = new RefreshToken();
        token.setUser(user);

        token.setExpiryDate(Instant.now().plusSeconds(3600));
        assertEquals(token, refreshTokenService.verifyExpiration(token));
    }

    @Test
    void verifyExpiration_WithExpiredToken_ShouldThrowExpiredRefreshTokenException() {
        User user = new User();
        user.setUserCode("sampleUserCode");

        RefreshToken token = new RefreshToken();
        token.setUser(user);

        token.setExpiryDate(Instant.now().minusSeconds(3600));
        assertThrows(ExpiredRefreshTokenException.class, () -> refreshTokenService.verifyExpiration(token));
        verify(refreshTokenRepository, times(1)).delete(token);
    }

    @Test
    void findByToken_WithExistingToken_ShouldReturnToken() {
        User user = new User();
        user.setUserCode("sampleUserCode");

        String tokenString = UUID.randomUUID().toString();
        RefreshToken token = new RefreshToken();
        token.setUser(user);

        when(refreshTokenRepository.findByToken(tokenString)).thenReturn(Optional.of(token));
        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenString);
        assertTrue(result.isPresent());
        assertEquals(token, result.get());
    }

    @Test
    void findByToken_WithNonExistingToken_ShouldReturnEmptyOptional() {
        String tokenString = UUID.randomUUID().toString();
        when(refreshTokenRepository.findByToken(tokenString)).thenReturn(Optional.empty());
        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenString);
        assertTrue(result.isEmpty());
    }
}