package com.bit.productservice.service;

import com.bit.productservice.service.serviceimpl.CloudinaryServiceImpl;
import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CloudinaryServiceTest {
    @Mock
    private Cloudinary cloudinary;

    @InjectMocks
    private CloudinaryServiceImpl cloudinaryService;

    private Cloudinary cloudinaryMock;
    private Uploader uploaderMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cloudinaryMock = mock(Cloudinary.class);
        uploaderMock = mock(Uploader.class);
    }

    @Test
    void upload_Success() throws IOException {
        Resource resource = new ClassPathResource("static/images/apple.jpg");
        byte[] fileContent = Files.readAllBytes(resource.getFile().toPath());

        MultipartFile multipartFile = new MockMultipartFile("file", "apple.jpg", "image/jpeg", fileContent);

        when(cloudinaryMock.uploader()).thenReturn(uploaderMock);

        Map<String, Object> uploadResult = new HashMap<>();
        when(uploaderMock.upload(any(File.class), any())).thenReturn(uploadResult);

        CloudinaryServiceImpl cloudinaryService = new CloudinaryServiceImpl();

        Map<String, String> result = cloudinaryService.upload(multipartFile);

        assertNotNull(result);
        assertTrue(result.containsKey("url"));
    }

    @Test
    void upload_Failure() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        doThrow(IOException.class).when(multipartFile).getBytes();

        Cloudinary cloudinaryMock = mock(Cloudinary.class);
        CloudinaryServiceImpl cloudinaryService = new CloudinaryServiceImpl();

        assertThrows(IOException.class, () -> cloudinaryService.upload(multipartFile));

        verifyNoInteractions(cloudinaryMock);
    }

    @Test
    void delete_Success() throws IOException {
        String imageId = "kkrqzpmpo66lzl4cioqv";

        when(cloudinary.uploader()).thenReturn(uploaderMock);

        Map<String, Object> dummyResult = new HashMap<>();
        when(uploaderMock.destroy(imageId, ObjectUtils.emptyMap())).thenReturn(dummyResult);

        cloudinaryService.delete(imageId);

        verify(cloudinary, times(1)).uploader();

        verify(uploaderMock, times(1)).destroy(imageId, ObjectUtils.emptyMap());
    }

    @Test
    void delete_Failure() throws IOException {
        String imageId = "image_id";

        when(cloudinary.uploader()).thenReturn(uploaderMock);

        doThrow(IOException.class).when(uploaderMock).destroy(imageId, ObjectUtils.emptyMap());

        assertThrows(IOException.class, () -> cloudinaryService.delete(imageId));
    }

}