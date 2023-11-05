package com.group5.ecommerce.service;

import com.group5.ecommerce.model.Cart;
import com.group5.ecommerce.model.CartItem;
import com.group5.ecommerce.model.Product;
import com.group5.ecommerce.model.User;
import com.group5.ecommerce.repository.CartItemRepository;
import com.group5.ecommerce.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserService userService;

    public Cart updateItemInCart(Product product, int quantity, Cart cart){
        List<CartItem> cartItems = cart.getCartItems();

        CartItem item = findCartItem(cartItems, product.getId());
        item.setQuantity(quantity);
        item.setTotalPrice(quantity * product.getPrice());
        cartItemRepository.save(item);

        int totalItem = totalItem(cartItems);
        double totalPrice = totalPrice(cartItems);

        cart.setTotalItem(totalItem);
        cart.setTotalPrice(totalPrice);

        return cartRepository.save(cart);
    }

    public Cart addItemToCart(Product product, int quantity, Long userId){
        User user = userService.getUserById(userId);
        Cart cart = user.getCart();

        if (cart == null) {
            cart = new Cart();
        }
        List<CartItem> cartItemList = cart.getCartItems();
        CartItem cartItem = find(cartItemList, product.getId());

        double unitPrice = product.getPrice();

        int itemQuantity = 0;
        if (cartItemList == null) {
            cartItemList = new ArrayList<>();
            if (cartItem == null) {
                cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setCart(cart);
                cartItem.setQuantity(quantity);
                cartItem.setTotalPrice(unitPrice);
                cartItem.setCart(cart);
                cartItemList.add(cartItem);
                cart.setCartItems(cartItemList);
                cartItemRepository.save(cartItem);
            } else {
                itemQuantity = cartItem.getQuantity() + quantity;
                cartItem.setQuantity(itemQuantity);
                cartItemRepository.save(cartItem);
            }
        } else {
            if (cartItem == null) {
                cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setCart(cart);
                cartItem.setQuantity(quantity);
                cartItem.setTotalPrice(unitPrice);
                cartItem.setCart(cart);
                cartItemList.add(cartItem);

                cartItemRepository.save(cartItem);
            } else {
                itemQuantity = cartItem.getQuantity() + quantity;
                cartItem.setQuantity(itemQuantity);
                cartRepository.save(cart);
                cartItemRepository.save(cartItem);
            }
        }
        cart.setCartItems(cartItemList);

        double totalPrice = totalPrice(cart.getCartItems());
        int totalItem = totalItem(cart.getCartItems());

        cart.setTotalPrice(totalPrice);
        cart.setTotalItem(totalItem);
        cart.setUser(user);

        return cartRepository.save(cart);
    }

    public Cart deleteItemFromCart(Product product, Cart cart){
        List<CartItem> cartItems = cart.getCartItems();
        CartItem item = findCartItem(cartItems, product.getId());
        cartItems.remove(item);

        cartItemRepository.delete(item);
        double totalPrice = totalPrice(cart.getCartItems());
        int totalItem = totalItem(cart.getCartItems());
        cart.setCartItems(cartItems);
        cart.setTotalPrice(totalPrice);
        cart.setTotalItem(totalItem);

        return cartRepository.save(cart);
    }

    public CartItem findCartItem(List<CartItem> cartItems, Long productId){

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

    private CartItem find(List<CartItem> cartItems, long productId) {
        if (cartItems == null) {
            return null;
        }
        CartItem cartItem = null;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                cartItem = item;
            }
        }
        return cartItem;
    }

    private double totalPrice(List<CartItem> cartItemList) {
        double totalPrice = 0.0;
        for (CartItem item : cartItemList) {
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
}
