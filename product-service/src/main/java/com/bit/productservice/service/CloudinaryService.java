package com.bit.productservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Interface for Cloudinary service.
 */
public interface CloudinaryService {
    /**
     * Uploads a file to Cloudinary.
     *
     * @param multipartFile the file to upload
     * @return a map containing information about the uploaded file
     * @throws IOException if an I/O exception occurs during the upload process
     */
    Map<String, String> upload(MultipartFile multipartFile) throws IOException;

    /**
     * Deletes a file from Cloudinary.
     *
     * @param id the ID of the file to delete
     * @throws IOException if an I/O exception occurs during the deletion process
     */
    void delete(String id) throws IOException;
}
