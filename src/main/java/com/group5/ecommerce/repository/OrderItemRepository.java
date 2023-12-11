package com.group5.ecommerce.repository;

import com.group5.ecommerce.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Page<OrderItem> getOrderItemByOrderId(Long orderId, Pageable pageable);

    Page<OrderItem> findAllByProduct_NameContaining(String productName, Pageable pageable);
}
