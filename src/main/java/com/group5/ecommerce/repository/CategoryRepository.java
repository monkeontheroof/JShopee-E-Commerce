package com.group5.ecommerce.repository;

import com.group5.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long>
{
    Category findByName(String name);
}
