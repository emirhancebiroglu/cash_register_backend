package com.bit.user_management_service.utils;

import com.bit.user_management_service.dto.kafka.UserCredentialsDTO;
import com.bit.user_management_service.dto.kafka.UserSafeDeletionDTO;
import com.bit.user_management_service.entity.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class CredentialsProducerTest {
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private CredentialsProducer credentialsProducer;

    @Test
    public void testSendMessage_UserCredentialsDTO() {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        Set<Role> roles = new HashSet<>();

        userCredentialsDTO.setId(1L);
        userCredentialsDTO.setUserCode("testCode");
        userCredentialsDTO.setPassword("testPassword");
        userCredentialsDTO.setRoles(roles);

        Mockito.when(kafkaTemplate.send(Mockito.anyString(), Mockito.any(UserCredentialsDTO.class)))
                .thenReturn(null);

        credentialsProducer.sendMessage("testTopic", userCredentialsDTO);

        Mockito.verify(kafkaTemplate).send("testTopic", userCredentialsDTO);
    }

    @Test
    public void testSendMessage_UserSafeDeletionDTO() {
        UserSafeDeletionDTO userSafeDeletionDTO = new UserSafeDeletionDTO();

        userSafeDeletionDTO.setId(1L);

        Mockito.when(kafkaTemplate.send(Mockito.anyString(), Mockito.any(UserSafeDeletionDTO.class)))
                .thenReturn(null);

        credentialsProducer.sendMessage("testTopic", userSafeDeletionDTO);

        Mockito.verify(kafkaTemplate).send("testTopic", userSafeDeletionDTO);
    }
}
