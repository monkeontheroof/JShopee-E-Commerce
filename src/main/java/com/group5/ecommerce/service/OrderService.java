package com.group5.ecommerce.service;

import com.group5.ecommerce.model.*;
import com.group5.ecommerce.repository.CartRepository;
import com.group5.ecommerce.repository.OrderItemRepository;
import com.group5.ecommerce.repository.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StoreService storeService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

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

    public void createOrderFromCart(Cart cart, User user) {
        List<Order> orders = new ArrayList<>();

        Map<UserStore, List<CartItem>> storeItemsMap = groupCartItemsByStore(cart);

        for (Map.Entry<UserStore, List<CartItem>> entry : storeItemsMap.entrySet()) {
            Order order = createAndSaveOrder(user, entry.getKey(), entry.getValue());
            orders.add(order);
        }

        orderRepository.saveAll(orders);
    }

    private Order createAndSaveOrder(User user, UserStore store, List<CartItem> cartItems) {
        Order order = new Order();
        orderRepository.save(order);
        order.setUser(user);
        order.setStore(store);
        order.setDate(LocalDate.now());
        order.setStatus("Pending");

        List<OrderItem> orderItems = createOrderItems(order, cartItems);
        orderItemRepository.saveAll(orderItems);
        order.setOrderItems(orderItems);

        // Tính toán và cập nhật totalPrice cho Order
        double totalPrice = orderItems.stream().mapToDouble(OrderItem::getTotalPrice).sum();
        order.setTotalPrice(totalPrice);

        orderRepository.save(order);

        return order;
    }

    private List<OrderItem> createOrderItems(Order order, List<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setTotalPrice(cartItem.getTotalPrice()); // Cập nhật giá cho từng OrderItem
                    return orderItem;
                })
                .collect(Collectors.toList());
    }

    private Map<UserStore, List<CartItem>> groupCartItemsByStore(Cart cart) {
        return cart.getCartItems().stream()
                .collect(Collectors.groupingBy(cartItem -> cartItem.getProduct().getStore()));
    }

    public void saveOrder(Order order, Long storeId){
        UserStore store = storeService.getStoreById(storeId);
        order.setStore(store);
        orderRepository.save(order);
    }

    public void deleteOrder(Long orderId){
        Order order = orderRepository.findById(orderId).get();
        order.getOrderItems().stream().forEach(item -> item.setOrder(null));
        order.setUser(null);
        order.setStore(null);
        orderRepository.delete(order);
    }
}
