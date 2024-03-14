package com.bit.productservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    Map upload(MultipartFile multipartFile) throws IOException;
    void delete(String id) throws IOException;
}
