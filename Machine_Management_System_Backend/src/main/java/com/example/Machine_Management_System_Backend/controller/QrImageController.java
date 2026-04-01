package com.example.Machine_Management_System_Backend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")  // ✅ Allow all origins for QR images
@RestController
@RequestMapping("/machines")
public class QrImageController {

    @GetMapping(value = "/qr/{machineId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQr(@PathVariable Long machineId) {
        try {
            Path path = Paths.get("./qrcodes/" + machineId + ".png");

            if (!Files.exists(path)) {
                // Return 404 with empty response
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(path);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);

        } catch (IOException e) {
            System.err.println("Error reading QR code: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/qr/base64/{machineId}")
    public ResponseEntity<Map<String, String>> getQrBase64(@PathVariable Long machineId) {
        try {
            Path path = Paths.get("./qrcodes/" + machineId + ".png");

            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(path);
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            Map<String, String> response = new HashMap<>();
            response.put("machineId", machineId.toString());
            response.put("base64", "data:image/png;base64," + base64Image);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            System.err.println("Error reading QR code: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}