package com.bit.productservice.service.serviceimpl;

import com.bit.productservice.service.CloudinaryService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryServiceImpl.class);
    Cloudinary cloudinary;

    public CloudinaryServiceImpl() {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("cloud_name", "djp1zp1jw");
        valuesMap.put("api_key", "948222386924368");
        valuesMap.put("api_secret", "5SsejtQT_OB4iQSrFDkr22mFYOg");
        cloudinary = new Cloudinary(valuesMap);
    }

    @Override
    public Map upload(MultipartFile multipartFile) throws IOException {
        File file = convert(multipartFile);
        logger.info("Uploading file: {}", file.getName());
        var result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
        if (!Files.deleteIfExists(file.toPath())) {
            throw new IOException("Failed to delete temporary file: " + file.getAbsolutePath());
        }
        logger.info("File uploaded successfully with result: {}", result);
        return result;
    }

    @Override
    public void delete(String id) throws IOException {
        logger.info("Deleting image with ID: {}", id);
        cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
        logger.info("Image deleted successfully with ID: {}", id);
    }

    private File convert(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (FileOutputStream fo = new FileOutputStream(file)) {
            fo.write(multipartFile.getBytes());
        }
        return file;
    }
}
