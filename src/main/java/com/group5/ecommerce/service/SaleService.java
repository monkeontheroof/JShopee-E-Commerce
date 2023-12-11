package com.group5.ecommerce.service;

import com.group5.ecommerce.model.Order;
import com.group5.ecommerce.model.OrderItem;
import com.group5.ecommerce.model.Product;
import com.group5.ecommerce.model.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleService {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StoreService storeService;

    public double getRevenueByProductId(Long productId, Long storeId) {
        Product product = productService.getProductById(productId).orElse(null);
        List<Order> orders = orderService.getAll();
        return orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .filter(item -> item.getProduct().equals(product))
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
    }

//    public double getRevenueByStoreId(Long storeId) {
//        UserStore store = storeService.getStoreById(storeId);
//        if (store != null) {
//            List<Order> orders = store.getOrders();
//
//        }
//    }
}
