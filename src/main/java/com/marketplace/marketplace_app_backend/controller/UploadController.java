package com.marketplace.marketplace_app_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.marketplace.marketplace_app_backend.services.SupabaseService;

@RestController
@RequestMapping("/upload_image")
public class UploadController {

    @Autowired
    private SupabaseService supabaseService;

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = supabaseService.uploadImage(file);
            return ResponseEntity.ok().body(new UploadResponse(url));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al subir archivo");
        }
    }

    record UploadResponse(String url) {}
}
