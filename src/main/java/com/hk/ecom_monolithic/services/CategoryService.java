package com.hk.ecom_monolithic.services;

import com.hk.ecom_monolithic.DTO.CategoryDTO;
import com.hk.ecom_monolithic.DTO.CategoryResponse;
import com.hk.ecom_monolithic.model.Category;

import java.util.List;

public interface CategoryService {

    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String  sortBy, String sortOrder);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long categoryId);
    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
