package com.group5.ecommerce.service;

import com.group5.ecommerce.model.*;
import com.group5.ecommerce.repository.OrderRepository;
import com.group5.ecommerce.repository.VoucherRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private StoreService storeService;

    @Autowired
    private OrderRepository orderRepository;

    public Voucher findById(Long id){
        return voucherRepository.findById(id).orElse(null);
    }

    public void createVoucher(Long storeId, Voucher createdVoucher){
        UserStore store = storeService.getStoreById(storeId);
        Voucher voucher = Voucher.builder()
                .code(RandomStringUtils.randomAlphanumeric(5).toUpperCase())
                .discountAmount(createdVoucher.getDiscountAmount() / 100.0)
                .expiryDate(createdVoucher.getExpiryDate())
                .name(createdVoucher.getName())
                .quantity(createdVoucher.getQuantity())
                .store(store)
                .build();
        saveVoucher(voucher);
    }

    public void applyVoucherToCartItem(CartItem cartItem, String voucherCode) {
        Voucher voucher = voucherRepository.findByCode(voucherCode);
        if(voucher != null && cartItem.getProduct().getStore().equals(voucher.getStore())){
            cartItem.setVoucher(voucher);

            double discountedPrice = cartItem.getTotalPrice() * (1 - voucher.getDiscountAmount());
            cartItem.setTotalPrice(discountedPrice);
        }
    }

    public Page<Voucher> findAllVouchersByStore(Long storeId, Pageable pageable) {
        return voucherRepository.findAllByStoreId(storeId, pageable);
    }

    public Voucher findByCode(String code) {
        return voucherRepository.findByCode(code);
    }

    public void saveVoucher(Voucher voucher){
        if(voucher != null){
            voucherRepository.save(voucher);
        }
    }

    public void deleteVoucherById(Long voucherId){
        voucherRepository.deleteById(voucherId);
    }
}
