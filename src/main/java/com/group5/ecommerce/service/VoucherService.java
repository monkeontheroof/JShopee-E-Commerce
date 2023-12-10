package com.group5.ecommerce.service;

import com.group5.ecommerce.model.CartItem;
import com.group5.ecommerce.model.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

//TODO: Bridge
public interface VoucherService {
    Voucher findById(Long id);
    void createVoucher(Long storeId, Voucher createdVoucher);
    void saveVoucher(Voucher voucher);
    void deleteVoucherById(Long voucherId);
    void applyVoucherToCartItem(CartItem cartItem, String voucherCode);
    Page<Voucher> findAllVouchersByStore(Long storeId, Pageable pageable);
    Voucher findByCode(String code);
}
