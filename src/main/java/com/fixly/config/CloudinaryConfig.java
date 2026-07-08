package com.fixly.config;

import com.cloudinary.Cloudinary;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryConfig.class);

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @PostConstruct
    public void verifyConfig() {
        log.info("=== Cloudinary Configuration Verification ===");
        log.info("cloud-name: {}", cloudName);
        log.info("api-key length: {}", apiKey != null ? apiKey.length() : "null");
        log.info("api-secret length: {}", apiSecret != null ? apiSecret.length() : "null");
        
        if (cloudName == null || cloudName.isBlank() || cloudName.contains("${")) {
            log.error("CRITICAL: cloudinary.cloud-name is unresolved or empty!");
        }
        if (apiKey == null || apiKey.isBlank() || apiKey.contains("${")) {
            log.error("CRITICAL: cloudinary.api-key is unresolved or empty!");
        }
        if (apiSecret == null || apiSecret.isBlank() || apiSecret.contains("${")) {
            log.error("CRITICAL: cloudinary.api-secret is unresolved or empty!");
        }
        log.info("=============================================");
    }

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(Map.of(
                "cloud_name", cloudName,
                "api_key",    apiKey,
                "api_secret", apiSecret,
                "secure",     true
        ));
    }
}
