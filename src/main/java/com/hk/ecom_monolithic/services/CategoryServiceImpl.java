package com.hk.ecom_monolithic.services;

import com.hk.ecom_monolithic.model.Category;
import com.hk.ecom_monolithic.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "category with "+categoryId+" not found"));

        categoryRepository.delete(savedCategory);
        return "Category with categoryId: " + categoryId + " has been deleted";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "category with " + categoryId + " not found"));

        category.setCategoryId(categoryId);
        savedCategory= categoryRepository.save(category);
        return savedCategory;

    }
}
