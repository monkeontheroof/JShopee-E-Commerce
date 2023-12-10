package com.group5.ecommerce.service;

import com.group5.ecommerce.model.CartItem;
import com.group5.ecommerce.model.UserStore;
import com.group5.ecommerce.model.Voucher;
import com.group5.ecommerce.pattern.factory.VoucherFactory;
import com.group5.ecommerce.pattern.factory.VoucherType;
import com.group5.ecommerce.repository.VoucherRepository;
import com.group5.ecommerce.service.impl.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class ConcreteVoucherImplementation implements VoucherServiceImplementation{
    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private StoreService storeService;

    @Override
    public Voucher findById(Long id){
        return voucherRepository.findById(id).orElse(null);
    }

    @Override
    public void createVoucher(Long storeId, Voucher createdVoucher){
        UserStore store = storeService.getStoreById(storeId);
        Voucher voucher = VoucherFactory.getVoucher(VoucherType.DISCOUNT,
                store,
                createdVoucher.getDiscountAmount());
        saveVoucher(voucher);
    }

    @Override
    public void saveVoucher(Voucher voucher){
        if(voucher != null){
            voucherRepository.save(voucher);
        }
    }

    @Override
    public void deleteVoucherById(Long voucherId){
        voucherRepository.deleteById(voucherId);
    }

    @Override
    public void applyVoucherToCartItem(CartItem cartItem, String voucherCode) {
        Voucher voucher = voucherRepository.findByCode(voucherCode);
        if(voucher != null && cartItem.getProduct().getStore().equals(voucher.getStore())){
            cartItem.setVoucher(voucher);

            double discountedPrice = cartItem.getTotalPrice() * (1 - voucher.getDiscountAmount());
            cartItem.setTotalPrice(discountedPrice);
        }
    }

    @Override
    public Page<Voucher> findAllVouchersByStore(Long storeId, Pageable pageable) {
        return voucherRepository.findAllByStoreId(storeId, pageable);
    }

    @Override
    public Voucher findByCode(String code) {
        return voucherRepository.findByCode(code);
    }
}
