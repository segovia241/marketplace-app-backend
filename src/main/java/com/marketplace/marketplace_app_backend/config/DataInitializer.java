// src/main/java/com/marketplace/marketplace_app_backend/config/DataInitializer.java
package com.marketplace.marketplace_app_backend.config;

import com.marketplace.marketplace_app_backend.model.Category;
import com.marketplace.marketplace_app_backend.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initCategories(CategoryRepository categoryRepository) {
        return args -> {
            String[] defaultCategories = {
                    "Electrónica",
                    "Ropa",
                    "Hogar",
                    "Juguetes",
                    "Libros",
                    "Deportes",
                    "Otros"
            };

            for (String name : defaultCategories) {
                // Solo crear si no existe
                categoryRepository.findByName(name).orElseGet(() -> {
                    Category category = new Category();
                    category.setName(name);
                    return categoryRepository.save(category);
                });
            }
        };
    }
}
