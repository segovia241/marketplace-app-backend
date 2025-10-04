package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.security.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/token")
public class TokenController {

    private final JwtUtil jwtUtil;

    public TokenController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/validate")
    public Map<String, Boolean> validateToken(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        if (token == null || token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token no proporcionado");
        }

        try {
            jwtUtil.extractEmail(token); // Si no lanza excepción, es válido
            return Collections.singletonMap("valid", true);
        } catch (ExpiredJwtException e) {
            // Token expirado
            return Collections.singletonMap("valid", false);
        } catch (JwtException e) {
            // Token inválido por otra razón
            return Collections.singletonMap("valid", false);
        }
    }
}
