package com.group5.ecommerce.service.impl;

import com.group5.ecommerce.model.*;
import com.group5.ecommerce.repository.CartItemRepository;
import com.group5.ecommerce.repository.CartRepository;
import com.group5.ecommerce.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

//TODO: Facade
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private ProductService productService;

    @Autowired //TODO: Dependency Injection
    @Qualifier("voucherServiceImpl")
    private VoucherService voucherService;

    public Cart getCartByUserId(long userId){
        User user = userServiceImpl.getUserById(userId);
        if(user.getCart() == null){
            Cart cart = new Cart();
            List<CartItem> cartItems = new ArrayList<>();
            cart.setUser(user);
            cartItemRepository.saveAll(cartItems);
            cart.setCartItems(cartItems);
            cartRepository.save(cart);
            return cart;
        }
        return user.getCart();
    }

    public Cart updateItemInCart(long productId, int quantity, long userId, String voucherCode){
        Product product = productService.getProductById(productId).get();

        Cart cart = userServiceImpl.getUserById(userId).getCart();
        List<CartItem> cartItems = cart.getCartItems();

        CartItem item = findCartItemByProductId(cartItems, product.getId());
        item.setQuantity(quantity);
        item.setTotalPrice(quantity * product.getPrice());
        voucherService.applyVoucherToCartItem(item, voucherCode);
        cartItemRepository.save(item);

        int totalItem = totalItem(cartItems);

        cart.setTotalItem(totalItem);
        cart.setTotalPrice();
        return cartRepository.save(cart);
    }

    @Transactional //TODO: Proxy
    public Cart addItemToCart(Product product, int quantity, long userId){
        User user = userServiceImpl.getUserById(userId);
        Cart cart = getCartByUserId(userId);

        List<CartItem> cartItemList = cart.getCartItems();
        CartItem cartItem = findCartItemByProductId(cartItemList, product.getId());

        double unitPrice = product.getPrice();

        int itemQuantity = 0;
        if (cartItemList.isEmpty()) {
            cartItemList = new ArrayList<>();
            cartItem = new CartItem();
            cartItem.setProduct(product);

            cartItem.setQuantity(quantity);
            cartItem.setTotalPrice(unitPrice * quantity);
            cartItem.setCart(cart);
            cartItemList.add(cartItem);
            cartItem.setCart(cart);
            cartItemRepository.save(cartItem);
        } else {
            if (cartItem == null) {
                cartItem = new CartItem();
                cartItem.setProduct(product);

                cartItem.setQuantity(quantity);
                cartItem.setTotalPrice(unitPrice * quantity);
                cartItem.setCart(cart);
                cartItemList.add(cartItem);
                cartItemRepository.save(cartItem);
            } else {
                itemQuantity = cartItem.getQuantity() + quantity;
                cartItem.setQuantity(itemQuantity);
                cartItem.setTotalPrice(itemQuantity * cartItem.getProduct().getPrice());
                cartItemRepository.save(cartItem);
            }
        }
        cart.setCartItems(cartItemList);

        int totalItem = totalItem(cart.getCartItems());

        cart.setTotalPrice();
        cart.setTotalItem(totalItem);
        cart.setUser(user);

        return cartRepository.save(cart);
    }

    public Cart findById(long id) {
        return cartRepository.findById(id).get();
    }

    public void deleteItemFromCart(Product product, Long userId){
        Cart cart = userServiceImpl.getUserById(userId).getCart();
        List<CartItem> cartItems = cart.getCartItems();
        CartItem item = findCartItemByProductId(cartItems, product.getId());
        int quantity = product.getQuantity() + item.getQuantity();
        product.setQuantity(quantity);
        cartItems.remove(item);

        cartItemRepository.delete(item);
        double totalPrice = totalPrice(cart.getCartItems());
        int totalItem = totalItem(cart.getCartItems());
        cart.setCartItems(cartItems);
        cart.setTotalPrice();
        cart.setTotalItem(totalItem);

        cartRepository.save(cart);
    }

    public CartItem findCartItemByProductId(List<CartItem> cartItems, Long productId){

        if(cartItems== null){
            return null;
        }

        CartItem cartItem = null;

        for(CartItem item : cartItems){
            if(item.getProduct().getId().equals(productId)){
                cartItem = item;
            }
        }

        return cartItem;
    }

    public void save(Cart cart){
        cartRepository.save(cart);
    }

    public int countCartItems(Cart cart){
        return cart.getCartItems().stream().mapToInt(CartItem::getQuantity).sum();
    }

    private double totalPrice(List<CartItem> cartItemList) {
        double totalPrice = 0.0;
        Iterator<CartItem> itemIterator = cartItemList.iterator(); //TODO: Iterator
        while (itemIterator.hasNext()){
            CartItem item = itemIterator.next();
            totalPrice += item.getProduct().getPrice() * item.getQuantity();
        }
        return totalPrice;
    }

    private int totalItem(List<CartItem> cartItemList) {
        int totalItem = 0;
        for (CartItem item : cartItemList) {
            totalItem += item.getQuantity();
        }
        return totalItem;
    }

    public void clear(Cart cart) {
        cart.getCartItems().clear();
    }
}
