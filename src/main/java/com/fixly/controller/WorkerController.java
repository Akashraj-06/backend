package com.fixly.controller;

import com.fixly.dto.NearbyWorkersDto;
import com.fixly.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService workerService;

    @GetMapping("/nearby")
    public ResponseEntity<List<NearbyWorkersDto>> getNearbyWorkers(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "10.0") Double radius
    ) {
        return ResponseEntity.ok(workerService.findNearbyWorkers(lat, lng, radius));
    }
}
