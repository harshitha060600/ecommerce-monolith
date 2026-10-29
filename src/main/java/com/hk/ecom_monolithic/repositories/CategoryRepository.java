package com.hk.ecom_monolithic.repositories;

import com.hk.ecom_monolithic.model.Category;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByCategoryName(@Size(min = 5, message = "category name must contain atleast 5 characters") String categoryName);
}
