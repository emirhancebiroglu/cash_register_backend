package com.bit.jwt_auth_service.controller;

import com.bit.jwt_auth_service.dto.LoginRequest;
import com.bit.jwt_auth_service.service.JwtService;
import com.bit.sharedClasses.dto.TokenValidationReq;
import com.bit.sharedClasses.entity.User;
import com.bit.sharedClasses.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class AuthControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    private String token;

    @BeforeEach
    public void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    public void testAuthenticateAndGetToken_With_Correct_Credentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("emirhan@hotmail.com", "emirhan");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testAuthenticateAndGetToken_With_Bad_Credentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("test@hotmail.com", "test");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testValidateToken_With_Correct_Token() throws Exception{
        Optional<User> user = userRepository.findByEmail("emirhan@hotmail.com");

        user.ifPresent(value -> token = jwtService.generateToken(value));

        TokenValidationReq request = new TokenValidationReq(token);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/validate-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void testValidateToken_With_Wrong_Token() throws Exception {
        TokenValidationReq request = new TokenValidationReq("dummyValue");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/validate-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testExtractUsername_With_Correct_Token() throws Exception{
        Optional<User> user = userRepository.findByEmail("emirhan@hotmail.com");

        user.ifPresent(value -> token = jwtService.generateToken(value));

        TokenValidationReq request = new TokenValidationReq(token);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/extract-username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("emirhan@hotmail.com"));
    }

    @Test
    public void testExtractUsername_With_Wrong_Token() throws Exception{
        TokenValidationReq request = new TokenValidationReq("dummyValue");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/extract-username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}
