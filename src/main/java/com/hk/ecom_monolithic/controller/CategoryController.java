package com.hk.ecom_monolithic.controller;

import com.hk.ecom_monolithic.model.Category;
import com.hk.ecom_monolithic.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
@RequestMapping("/api")
@RestController
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //@GetMapping("/public/categories")
    @RequestMapping(value="/public/categories",method = RequestMethod.GET)
    public ResponseEntity<List<Category>> getCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<String> createCategory(@RequestBody Category category) {
       categoryService.createCategory(category);
       return new ResponseEntity<>("Category added", HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        try {
            String status = categoryService.deleteCategory(categoryId);
            //return new ResponseEntity<>(status, HttpStatus.OK);
            return ResponseEntity.ok(status);
        }catch(ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@RequestBody Category category,
                                                 @PathVariable Long categoryId) {
        try{
            Category savedCategory = categoryService.updateCategory(category,categoryId);
            return new ResponseEntity<>("Category with category id: " + categoryId + " updated", HttpStatus.OK);
        }catch(ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
    }
}
