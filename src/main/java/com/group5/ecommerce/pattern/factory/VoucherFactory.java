package com.group5.ecommerce.pattern.factory;

import com.group5.ecommerce.model.UserStore;
import com.group5.ecommerce.model.Voucher;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;

//TODO: Factory Method
public class VoucherFactory {
    private VoucherFactory(){

    }

    public static final Voucher getVoucher(VoucherType voucherType, UserStore store, double discount){
        switch (voucherType){
            case FREESHIP:
                return Voucher.builder()
                        .code(RandomStringUtils.randomAlphanumeric(5).toUpperCase())
                        .name("Miễn phí giao hàng")
                        .discountAmount(0.0)
                        .store(store)
                        .expiryDate(LocalDate.now().plusDays(7))
                        .quantity(10)
                        .build();
            case DISCOUNT:
                return Voucher.builder()
                        .code(RandomStringUtils.randomAlphanumeric(5).toUpperCase())
                        .name("Giảm giá sản phẩm - " + discount + "%")
                        .discountAmount(discount / 100.0)
                        .store(store)
                        .expiryDate(LocalDate.now().plusDays(7))
                        .quantity(10)
                        .build();
            default:
                throw new RuntimeException("Loại voucher không hợp lệ!");
        }
    }
}
