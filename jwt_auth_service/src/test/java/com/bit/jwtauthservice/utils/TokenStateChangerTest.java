package com.bit.jwtauthservice.utils;

import com.bit.jwtauthservice.entity.Token;
import com.bit.jwtauthservice.entity.User;
import com.bit.jwtauthservice.repository.TokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenStateChangerTest {
    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenStateChanger tokenStateChanger;

    @Test
    void saveUserToken_ShouldSaveToken() {
        User user = new User();
        String jwtToken = "sampleToken";

        tokenStateChanger.saveUserToken(user, jwtToken);

        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    void revokeAllUserTokens_ShouldRevokeTokens() {
        User user = new User();
        Token token1 = new Token();
        Token token2 = new Token();
        List<Token> tokens = new ArrayList<>();
        tokens.add(token1);
        tokens.add(token2);

        when(tokenRepository.findAllValidTokensByUser(user.getId())).thenReturn(tokens);

        tokenStateChanger.revokeAllUserTokens(user);

        verify(tokenRepository, times(1)).saveAll(tokens);
    }

    @Test
    void revokeAllUserTokens_ShouldNotRevokeTokensIfEmpty() {
        User user = new User();
        List<Token> tokens = new ArrayList<>();

        when(tokenRepository.findAllValidTokensByUser(user.getId())).thenReturn(tokens);

        tokenStateChanger.revokeAllUserTokens(user);

        verify(tokenRepository, times(1)).findAllValidTokensByUser(user.getId());
        verifyNoMoreInteractions(tokenRepository);
    }
}