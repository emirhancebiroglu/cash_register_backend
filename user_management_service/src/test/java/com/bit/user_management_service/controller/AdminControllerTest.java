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
    public void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void canCreateUserAsAdmin() throws Exception {
        mockMvc.perform(post("/api/users/admin/add-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"ismet\"," +
                                " \"lastName\": \"genc\"," +
                                " \"userCode\": \"emirhan21@hotmail.com\"," +
                                " \"password\": \"Emirhan2165\"," +
                                " \"roles\": [\"ROLE_ADMIN\"]}"))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser
    void cannotCreateUserIfNotAnAdmin() throws Exception{
        mockMvc.perform(post("/api/users/admin/add-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\": \"Emirhan\"," +
                        " \"lastName\": \"Cebiroglu\"," +
                        " \"userCode\": \"emirhanebiroglu211@hotmail.com\"," +
                        " \"password\": \"emirhan\"," +
                        " \"roles\": [\"ROLE_CASHIER\"]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void canUpdateUserAsAdmin() throws Exception{
        mockMvc.perform(put("/api/users/admin/update-user/{user_id}", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\": \"newEmirhan\"," +
                        " \"lastName\": \"newCebiroglu\"," +
                        " \"userCode\": \"newemirhanebiroglu21@hotmail.com\"," +
                        " \"password\": \"newEmirhan2165\"," +
                        " \"roles\": [\"ROLE_CASHIER\"]}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser()
    void cannotUpdateUserIfNotAnAdmin() throws Exception{
        mockMvc.perform(put("/api/users/admin/update-user/{user_id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"Emirhan\"," +
                                " \"lastName\": \"Cebiroglu\"," +
                                " \"userCode\": \"emirhanebiroglu21@hotmail.com\"," +
                                " \"password\": \"Emirhan2165\"," +
                                " \"roles\": [\"CASHIER\"]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void canDeleteUserAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/admin/delete-user/{user_id}", 8L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser()
    void cannotDeleteUserIfNotAnAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/admin/delete-user/{user_id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
