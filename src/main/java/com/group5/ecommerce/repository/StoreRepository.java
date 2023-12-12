package com.group5.ecommerce.repository;

import com.group5.ecommerce.model.UserStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<UserStore, Long> {
    UserStore findByUserId(Long userId);

    Page<UserStore> findAll(Pageable pageable);
}
