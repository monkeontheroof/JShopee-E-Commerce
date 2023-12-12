package com.group5.ecommerce.repository;

import com.group5.ecommerce.model.Status;
import com.group5.ecommerce.model.User;
import com.group5.ecommerce.model.UserStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAll(Pageable pageable);
    Optional<User> findByEmail(String email);

    List<User> findAllByStatus(Status status);
}
