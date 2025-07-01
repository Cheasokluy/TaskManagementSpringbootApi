package com.taskManagement.controller;

import com.taskManagement.entity.TestEntity;
import com.taskManagement.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor

public class TestController {
    private final TestRepository testRepository;

    @GetMapping("/connection")
    public ResponseEntity<String> testConnection() {
        try {
            TestEntity test = new TestEntity();
            test.setMessage("Database connection successful!");
            testRepository.save(test);
            return ResponseEntity.ok("✅ Database connection is working! Test record created.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("❌ Database connection failed: " + e.getMessage());
        }
    }

    @GetMapping("/records")
    public ResponseEntity<List<TestEntity>> getAllTestRecords() {
        List<TestEntity> records = testRepository.findAll();
        return ResponseEntity.ok(records);
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<String> cleanupTestRecords() {
        testRepository.deleteAll();
        return ResponseEntity.ok("🧹 Test records cleaned up!");
    }

}
