package com.group5.ecommerce.repository;

import com.group5.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByCategoryId(Long id);

    @Query("SELECT p FROM Product p JOIN p.category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Product> findAllByCategoryName(String name);

    Page<Product> findAllByStoreId(Long storeId, Pageable pageRequest);

    List<Product> findByQuantityLessThan(int quantity);
}
