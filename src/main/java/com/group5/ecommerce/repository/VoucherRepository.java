package com.group5.ecommerce.repository;

import com.group5.ecommerce.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    List<Voucher> findAllByStoreId(Long storeId);
}
