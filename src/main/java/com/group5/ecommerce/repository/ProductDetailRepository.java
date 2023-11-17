package com.group5.ecommerce.repository;

import com.group5.ecommerce.model.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {

    Page<ProductDetail> getProductDetailsByProductId(Long productId, Pageable pageRequest);
}
