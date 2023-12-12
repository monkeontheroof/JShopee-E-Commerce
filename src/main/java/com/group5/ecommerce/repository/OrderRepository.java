package com.group5.ecommerce.repository;

import com.group5.ecommerce.model.Order;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByStoreId(Long storeId, Pageable pageable);
    List<Order> findAllByStoreId(Long storeId);
    List<Order> findAllByUserId(Long userId);
    Page<Order> findAll(Pageable pageable);
}
