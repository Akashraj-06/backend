package com.fixly.service;

import com.fixly.dto.AuthRequest;
import com.fixly.dto.AuthResponse;
import com.fixly.dto.RegisterRequest;
import com.fixly.entity.ServiceCategory;
import com.fixly.entity.User;
import com.fixly.entity.Worker;
import com.fixly.repository.ServiceCategoryRepository;
import com.fixly.repository.UserRepository;
import com.fixly.repository.WorkerRepository;
import com.fixly.security.CustomUserDetails;
import com.fixly.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .build();
        
        userRepository.save(user);

        if (request.getRole() == User.Role.WORKER) {
            ServiceCategory category = serviceCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Service Category not found"));

            Worker worker = Worker.builder()
                    .user(user)
                    .serviceCategory(category)
                    .latitude(request.getLatitude() != null ? request.getLatitude() : 0.0)
                    .longitude(request.getLongitude() != null ? request.getLongitude() : 0.0)
                    .isAvailable(true)
                    .rating(0.0)
                    .totalJobs(0)
                    .build();
            workerRepository.save(worker);
        }

        String jwtToken = jwtUtil.generateToken(new CustomUserDetails(user));
        return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        String jwtToken = jwtUtil.generateToken(new CustomUserDetails(user));
        return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
