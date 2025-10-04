package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.Category;
import com.marketplace.marketplace_app_backend.repository.CategoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository repository;

    public CategoryController(CategoryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Category> getCategoryById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @GetMapping("/name/{name}")
    public Optional<Category> getCategoryByName(@PathVariable String name) {
        return repository.findByName(name);
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return repository.save(category);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category updatedCategory) {
        return repository.findById(id)
                .map(category -> {
                    category.setName(updatedCategory.getName());
                    return repository.save(category);
                })
                .orElseGet(() -> {
                    updatedCategory.setCategoryId(id);
                    return repository.save(updatedCategory);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        repository.deleteById(id);
    }
}