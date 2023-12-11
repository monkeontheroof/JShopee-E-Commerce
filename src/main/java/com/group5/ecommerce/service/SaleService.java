package com.group5.ecommerce.service;

import com.group5.ecommerce.model.Order;
import com.group5.ecommerce.model.OrderItem;
import com.group5.ecommerce.model.Product;
import com.group5.ecommerce.model.UserStore;
import com.group5.ecommerce.repository.OrderItemRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public double getRevenueByProductId(Long productId, Long storeId) {
        Product product = productService.getProductById(productId).orElse(null);
        List<Order> orders = orderService.getAllOrdersByStoreId(storeId);
        return orders.stream()
                .mapToDouble(order -> {
                    double totalPrice = order.getTotalPrice() / 1.04;
                    return order.getTotalPrice() - totalPrice;
                })
                .sum();
//                .flatMap(order -> order.getOrderItems().stream())
//                .filter(item -> item.getProduct().equals(product))
//                .mapToDouble(OrderItem::getTotalPrice)
//                .sum();
    }

    public Page<OrderItem> getOrderItemsByProductName(String productName, Long storeId,Pageable pageable){
        List<OrderItem> orderItems = getAllOrderItems(storeId, pageable).stream()
                .filter(item -> item.getProduct().getName().contains(productName.trim()))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), orderItems.size());

        return new PageImpl<>(orderItems.subList(start, end), pageable, orderItems.size());
    }

    public double calculateTotalRevenue(List<OrderItem> orderItems) {
        return orderItems.stream().mapToDouble(OrderItem::getTotalPrice).sum();
    }

    public Page<OrderItem> getAllOrderItems(Long storeId, Pageable pageable){
        UserStore store = storeService.getStoreById(storeId);
        List<OrderItem> orderItems = store.getOrders().stream()
                .flatMap(order -> order.getOrderItems().stream())
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), orderItems.size());

        return new PageImpl<>(orderItems.subList(start, end), pageable, orderItems.size());
    }

//    public double getRevenueByStoreId(Long storeId) {
//        UserStore store = storeService.getStoreById(storeId);
//        if (store != null) {
//            List<Order> orders = store.getOrders();
//
//        }
//    }
}
