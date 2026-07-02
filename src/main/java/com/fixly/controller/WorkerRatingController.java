package com.fixly.controller;

import com.fixly.dto.RatingRequestDto;
import com.fixly.dto.RatingResponseDto;
import com.fixly.security.JwtUtil;
import com.fixly.service.WorkerRatingService;
import com.fixly.repository.UserRepository;
import com.fixly.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RestController
@RequestMapping("/api/worker-ratings")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WorkerRatingController {

    @Autowired
    private WorkerRatingService workerRatingService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<RatingResponseDto> submitRating(
            @RequestHeader("Authorization") String token,
            @RequestBody RatingRequestDto requestDto) {
        
        String jwt = token.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
            
        Long customerId = user.getId();
        
        RatingResponseDto responseDto = workerRatingService.submitRating(customerId, requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
