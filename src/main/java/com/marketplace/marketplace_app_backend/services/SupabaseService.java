package com.marketplace.marketplace_app_backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@Service
public class SupabaseService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.bucket}")
    private String bucketName;

    @Value("${supabase.key}")
    private String supabaseKey;

    public String uploadImage(MultipartFile file) throws Exception {
        // ===============================
        // 1️⃣ Obtener nombre original y extensión
        // ===============================
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            originalName = "file";
        }

        String extension = "";
        int dotIndex = originalName.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < originalName.length() - 1) {
            extension = originalName.substring(dotIndex).toLowerCase();
        }

        // ===============================
        // 2️⃣ Generar hash hexadecimal corto
        // ===============================
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest((originalName + UUID.randomUUID()).getBytes(StandardCharsets.UTF_8));

        // Convertir los primeros 8 bytes a hexadecimal (16 chars)
        StringBuilder hexName = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            hexName.append(String.format("%02x", hashBytes[i]));
        }

        // Nombre final: ejemplo → "a3b9d1e47c2f8912.jpg"
        String safeFileName = hexName + extension;

        // ===============================
        // 3️⃣ Construir URL de subida
        // ===============================
        String encodedFileName = URLEncoder.encode(safeFileName, StandardCharsets.UTF_8);
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + encodedFileName;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .header("Authorization", "Bearer " + supabaseKey)
                .header("Content-Type", file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                .PUT(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // ===============================
        // 4️⃣ Validar respuesta y retornar URL pública
        // ===============================
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + encodedFileName;
        } else {
            throw new RuntimeException("Error al subir a Supabase (" + response.statusCode() + "): " + response.body());
        }
    }
}
