package com.group5.ecommerce.repository;

import com.group5.ecommerce.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long>
{
    Category findByName(String name);

    Page<Category> findAllByStoreId(Long storeId, Pageable pageable);

    List<Category> findAllByNameContaining(String name);
}
