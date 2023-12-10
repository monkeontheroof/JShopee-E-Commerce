package com.group5.ecommerce.service.impl;

import com.group5.ecommerce.pattern.factory.VoucherFactory;
import com.group5.ecommerce.pattern.factory.VoucherType;
import com.group5.ecommerce.model.*;
import com.group5.ecommerce.repository.OrderRepository;
import com.group5.ecommerce.repository.VoucherRepository;
import com.group5.ecommerce.service.VoucherService;
import com.group5.ecommerce.service.VoucherServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    private VoucherServiceImplementation voucherServiceImplementation;

    @Override
    public Voucher findById(Long id) {
        return voucherServiceImplementation.findById(id);
    }

    @Override
    public void createVoucher(Long storeId, Voucher createdVoucher) {
        voucherServiceImplementation.createVoucher(storeId, createdVoucher);
    }

    @Override
    public void saveVoucher(Voucher voucher) {
        voucherServiceImplementation.saveVoucher(voucher);
    }

    @Override
    public void deleteVoucherById(Long voucherId) {
        voucherServiceImplementation.deleteVoucherById(voucherId);
    }

    @Override
    public void applyVoucherToCartItem(CartItem cartItem, String voucherCode) {
        voucherServiceImplementation.applyVoucherToCartItem(cartItem, voucherCode);
    }

    @Override
    public Page<Voucher> findAllVouchersByStore(Long storeId, Pageable pageable) {
        return voucherServiceImplementation.findAllVouchersByStore(storeId, pageable);
    }

    @Override
    public Voucher findByCode(String code) {
        return voucherServiceImplementation.findByCode(code);
    }
}
