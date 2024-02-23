package com.bit.user_management_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class AdminControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddUserAsAdmin() throws Exception {
        mockMvc.perform(post("/api/users/admin/add-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"emir\"," +
                                " \"lastName\": \"cebiroglu\"," +
                                " \"email\": \"emirhan12@hotmail.com\"," +
                                " \"roles\": [\"ROLE_CASHIER\"]}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void testCannotCreateUserIfNotAnAdmin() throws Exception {
        mockMvc.perform(post("/api/users/admin/add-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"emirhan\"," +
                                " \"lastName\": \"cebiroglu\"," +
                                " \"email\": \"emirhan@hotmail.com\"," +
                                " \"roles\": [\"ROLE_CASHIER\"]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUserAsAdmin() throws Exception {
        mockMvc.perform(put("/api/users/admin/update-user/{user_id}", 6L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"emirhan\"," +
                                " \"lastName\": \"cebiroglu\"," +
                                " \"email\": \"emirhan12@hotmail.com\"," +
                                " \"roles\": [\"ROLE_ADMIN\"]}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testCannotUpdateUserIfNotAnAdmin() throws Exception {
        mockMvc.perform(put("/api/users/admin/update-user/{user_id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"newEmirhan\"," +
                                " \"lastName\": \"newCebiroglu\"," +
                                " \"userCode\": \"newemirhan@hotmail.com\"," +
                                " \"password\": \"newEmirhan2165\"," +
                                " \"roles\": [\"ROLE_CASHIER\"]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUserAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/admin/delete-user/{user_id}", 6L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testCannotDeleteUserIfNotAnAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/admin/delete-user/{user_id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
