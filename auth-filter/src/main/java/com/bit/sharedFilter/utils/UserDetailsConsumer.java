package com.bit.sharedFilter.utils;

import com.bit.sharedFilter.dto.UserDetailsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@EnableAsync
@RequiredArgsConstructor
public class UserDetailsConsumer {
    private UserDetailsDTO cachedUserDetails;

    public synchronized void updateCachedUserDetails(UserDetailsDTO userDetailsDTO) {
        this.cachedUserDetails = userDetailsDTO;
    }

    public synchronized UserDetailsDTO getCachedUserDetails() {
        return cachedUserDetails;
    }
    private final BlockingQueue<UserDetailsDTO> userDetailsQueue = new LinkedBlockingQueue<>();

    public UserDetailsDTO consumeUserDetails() throws InterruptedException {
        return userDetailsQueue.take();
    }

    @Async
    @KafkaListener(topics = "user-details", groupId = "user-credentials")
    public void listen(@Payload UserDetailsDTO userDetailsDTO) {
        try {
            userDetailsQueue.put(userDetailsDTO);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
