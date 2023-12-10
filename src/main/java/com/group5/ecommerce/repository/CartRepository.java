package com.group5.ecommerce.repository;

import com.group5.ecommerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Facade
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart getCartByUserId(Long userId);
}
