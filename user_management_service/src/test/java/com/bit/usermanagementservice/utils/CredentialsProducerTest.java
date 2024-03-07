package com.bit.usermanagementservice.utils;

import com.bit.usermanagementservice.dto.kafka.UserCredentialsDTO;
import com.bit.usermanagementservice.dto.kafka.UserReactivateDTO;
import com.bit.usermanagementservice.dto.kafka.UserSafeDeletionDTO;
import com.bit.usermanagementservice.dto.kafka.UserUpdateDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashSet;

@ExtendWith(MockitoExtension.class)
class CredentialsProducerTest {
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private CredentialsProducer credentialsProducer;

    @Test
    void sendMessage_UserCredentialsDTO() {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO(
                1L,
                "testemail@hotmail.com",
                "testCode",
                "testPassword",
                new HashSet<>(),
                false
        );

        Mockito.when(kafkaTemplate.send(Mockito.anyString(), Mockito.any(UserCredentialsDTO.class)))
                .thenReturn(null);

        credentialsProducer.sendMessage("testTopic", userCredentialsDTO);

        Mockito.verify(kafkaTemplate).send("testTopic", userCredentialsDTO);
    }

    @Test
    void sendMessage_UserSafeDeletionDTO() {
        UserSafeDeletionDTO userSafeDeletionDTO = new UserSafeDeletionDTO(
                1L,
                true
        );

        Mockito.when(kafkaTemplate.send(Mockito.anyString(), Mockito.any(UserSafeDeletionDTO.class)))
                .thenReturn(null);

        credentialsProducer.sendMessage("testTopic", userSafeDeletionDTO);

        Mockito.verify(kafkaTemplate).send("testTopic", userSafeDeletionDTO);
    }

    @Test
    void sendMessage_UserUpdateDTO() {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(
                1L,
                "testemail@gmail.com",
                "ACS3466321",
                new HashSet<>()
        );

        Mockito.when(kafkaTemplate.send(Mockito.anyString(), Mockito.any(UserUpdateDTO.class)))
                .thenReturn(null);

        credentialsProducer.sendMessage("testTopic", userUpdateDTO);

        Mockito.verify(kafkaTemplate).send("testTopic", userUpdateDTO);
    }

    @Test
    void sendMessage_UserReactivateDTO() {
        UserReactivateDTO userReactivateDTO = new UserReactivateDTO(
                1L,
                "password",
                false
        );

        Mockito.when(kafkaTemplate.send(Mockito.anyString(), Mockito.any(UserReactivateDTO.class)))
                .thenReturn(null);

        credentialsProducer.sendMessage("testTopic", userReactivateDTO);

        Mockito.verify(kafkaTemplate).send("testTopic", userReactivateDTO);
    }
}
