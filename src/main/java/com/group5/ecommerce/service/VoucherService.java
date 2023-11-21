package com.group5.ecommerce.service;

import com.group5.ecommerce.model.Order;
import com.group5.ecommerce.model.UserStore;
import com.group5.ecommerce.model.Voucher;
import com.group5.ecommerce.repository.OrderRepository;
import com.group5.ecommerce.repository.VoucherRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Voucher createVoucher(Long storeId, String name, double discount, int quantity){
        UserStore store = storeService.getStoreById(storeId);
        return Voucher.builder()
                .code(RandomStringUtils.randomAlphanumeric(5).toUpperCase())
                .discountAmount(discount)
                .expiryDate(LocalDateTime.now())
                .name(name)
                .quantity(quantity)
                .store(store)
                .build();
    }

    public Order applyVoucherToOrder(Long orderId, Long voucherId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        Voucher voucher = voucherRepository.findById(voucherId).orElse(null);

        if (order != null && voucher != null && voucher.getQuantity() > 0) {
            // Áp dụng giảm giá từ voucher
            double discountAmount = order.getTotalPrice() * (voucher.getDiscountAmount() / 100);
            double discountedTotalPrice = order.getTotalPrice() - discountAmount;
            order.setTotalPrice(discountedTotalPrice);

            // Giảm quantity của voucher
            voucher.setQuantity(voucher.getQuantity() - 1);
            voucherRepository.save(voucher);
            orderRepository.save(order);
        }

        return order;
    }

    public List<Voucher> findAllVouchersByStore(Long storeId) {
        return voucherRepository.findAllByStoreId(storeId);
    }

    public Voucher findByCode(String code) {
        return voucherRepository.findByCode(code);
    }

    public void saveVoucher(Voucher voucher){
        if(voucher != null){
            voucherRepository.save(voucher);
        }
    }

    public void deleteVoucher(Voucher voucher){
        if(voucher != null){
            voucherRepository.delete(voucher);
        }
    }
}
