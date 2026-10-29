package com.hk.ecom_monolithic.controller;

import com.hk.ecom_monolithic.DTO.CategoryDTO;
import com.hk.ecom_monolithic.DTO.CategoryResponse;
import com.hk.ecom_monolithic.config.AppConstants;
import com.hk.ecom_monolithic.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //@GetMapping("/public/categories")
    @RequestMapping(value="/public/categories",method = RequestMethod.GET)
    public ResponseEntity<CategoryResponse> getCategories(
            @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
       CategoryDTO savedCategory = categoryService.createCategory(categoryDTO);
       return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId) {
        CategoryDTO categoryDTO = categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                 @PathVariable Long categoryId) {

        CategoryDTO savedCategoryDTO = categoryService.updateCategory(categoryDTO,categoryId);
            return new ResponseEntity<>(savedCategoryDTO, HttpStatus.OK);

    }
}
