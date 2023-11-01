package com.group5.ecommerce.service;

import com.group5.ecommerce.model.Order;
import com.group5.ecommerce.model.User;
import com.group5.ecommerce.model.UserStore;
import com.group5.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StoreService storeService;

    public List<Order> getAllOrdersByStoreId(Long storeId){
        return orderRepository.findAllByStoreId(storeId);
    }

    public List<User> getCustomersPurchasedFromStore(Long storeId){
        List<Order> orders = getAllOrdersByStoreId(storeId);
        return orders.stream()
                .map(Order::getUser)
                .distinct()
                .collect(Collectors.toList());
    }

    public Order getOrderById(Long orderId){
        return orderRepository.findById(orderId).get();
    }

    public void saveOrder(Order order, Long storeId){
        UserStore store = storeService.getStoreById(storeId);
        order.setStore(store);
        orderRepository.save(order);
    }

    public void deleteOrder(Long orderId){
        orderRepository.deleteById(orderId);
    }
}
