package com.fixly.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fixly.exception.ImageUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

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

        try (InputStream inputStream = file.getInputStream()) {
            Map<?, ?> result = cloudinary.uploader().upload(
                    inputStream,
                    ObjectUtils.asMap(
                            "folder",          "fixly/service-requests",
                            "resource_type",   "image",
                            "use_filename",    false,
                            "unique_filename", true
                    )
            );
            
            String secureUrl = (String) result.get("secure_url");
            if (secureUrl == null || secureUrl.isBlank()) {
                throw new ImageUploadException("Cloudinary did not return a secure URL");
            }
            return secureUrl;
        } catch (IOException e) {
            throw new ImageUploadException("Failed to upload image to Cloudinary: " + e.getMessage(), e);
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
