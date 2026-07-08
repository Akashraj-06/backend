package com.fixly.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fixly.exception.ImageUploadException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);

    private static final long   MAX_FILE_SIZE   = 5 * 1024 * 1024L; // 5 MB
    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp"
    );

    private final Cloudinary cloudinary;

    /**
     * Validates and uploads a MultipartFile to Cloudinary.
     *
     * @param file the image to upload
     * @return the secure Cloudinary URL of the uploaded image
     */
    public String upload(MultipartFile file) {
        validateFile(file);

        log.info("Attempting file upload. Name: {}, Size: {}, Type: {}", 
                file.getOriginalFilename(), file.getSize(), file.getContentType());

        try {
            byte[] fileBytes = file.getBytes();
            log.info("File bytes loaded. Length: {}", fileBytes.length);

            Map<?, ?> result = cloudinary.uploader().upload(
                    fileBytes,
                    ObjectUtils.asMap(
                            "folder",          "fixly/service-requests",
                            "resource_type",   "image",
                            "use_filename",    false,
                            "unique_filename", true
                    )
            );

            log.info("Cloudinary upload response: {}", result);

            String secureUrl = (String) result.get("secure_url");
            if (secureUrl == null || secureUrl.isBlank()) {
                throw new ImageUploadException("Cloudinary did not return a secure URL");
            }
            return secureUrl;
        } catch (IOException e) {
            log.error("IOException reading file upload payload", e);
            throw new ImageUploadException("Failed to upload image to Cloudinary: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected exception during Cloudinary SDK call", e);
            throw new ImageUploadException("Unexpected error during Cloudinary upload: " + e.getMessage(), e);
        }
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size cannot exceed 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Only JPG, PNG and WEBP images are allowed");
        }
    }
}
