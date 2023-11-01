package com.group5.ecommerce.repository;

import com.group5.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long>
{
    Category findByName(String name);

    List<Category> findAllByStoreId(Long storeId);
}
