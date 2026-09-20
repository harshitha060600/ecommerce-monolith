package com.hk.ecom_monolithic.services;

import com.hk.ecom_monolithic.model.Category;

import java.util.List;

public interface CategoryService {

    List <Category> getAllCategories();
    void createCategory(Category category);
    String deleteCategory(Long categoryId);
}
