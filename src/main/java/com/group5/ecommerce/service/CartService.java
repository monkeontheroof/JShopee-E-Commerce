package com.group5.ecommerce.service;

import com.group5.ecommerce.model.Cart;
import com.group5.ecommerce.model.CartItem;
import com.group5.ecommerce.model.Product;
import com.group5.ecommerce.model.User;
import com.group5.ecommerce.repository.CartItemRepository;
import com.group5.ecommerce.repository.CartRepository;
import com.group5.ecommerce.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    public Cart getCartByUserId(long userId){
        User user = userService.getUserById(userId);
        if(user.getCart() == null){
            Cart cart = new Cart();
            List<CartItem> cartItems = new ArrayList<>();
            cart.setUser(user);
            cart.setCartItems(cartItems);
            cartRepository.save(cart);
            return cart;
        }
        return cartRepository.getCartByUserId(userId);
    }

    public Cart updateItemInCart(long productId, int quantity, long userId){
        Product product = productService.getProductById(productId).get();

        Cart cart = userService.getUserById(userId).getCart();
        List<CartItem> cartItems = cart.getCartItems();

        CartItem item = findCartItemByProductId(cartItems, product.getId());
        item.setQuantity(quantity);
        item.setTotalPrice(quantity * product.getPrice());
        cartItemRepository.save(item);

        int totalItem = totalItem(cartItems);
        double totalPrice = totalPrice(cartItems);

        cart.setTotalItem(totalItem);
        cart.setTotalPrice(totalPrice);

        return cartRepository.save(cart);
    }

    public Cart addItemToCart(Product product, int quantity, long userId){
        User user = userService.getUserById(userId);
        Cart cart = getCartByUserId(userId);

        List<CartItem> cartItemList = cart.getCartItems();
        CartItem cartItem = findCartItemByProductId(cartItemList, product.getId());

        double unitPrice = product.getPrice();

        int itemQuantity = 0;
        if (cartItemList == null) {
            cartItemList = new ArrayList<>();
            cartItem = new CartItem();
            cartItem.setProduct(product);

            cartItem.setQuantity(quantity);
            cartItem.setTotalPrice(unitPrice);
            cartItem.setCart(cart);
            cartItemList.add(cartItem);
            cartItem.setCart(cart);
            cartItemRepository.save(cartItem);
        } else {
            if (cartItem == null) {
                cartItem = new CartItem();
                cartItem.setProduct(product);

                cartItem.setQuantity(quantity);
                cartItem.setTotalPrice(unitPrice);
                cartItem.setCart(cart);
                cartItemList.add(cartItem);
                cartItemRepository.save(cartItem);
            } else {
                itemQuantity = cartItem.getQuantity() + quantity;
                cartItem.setQuantity(itemQuantity);
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

    public void deleteItemFromCart(Product product, Long userId){
        Cart cart = userService.getUserById(userId).getCart();
        List<CartItem> cartItems = cart.getCartItems();
        CartItem item = findCartItemByProductId(cartItems, product.getId());
        cartItems.remove(item);

        cartItemRepository.delete(item);
        double totalPrice = totalPrice(cart.getCartItems());
        int totalItem = totalItem(cart.getCartItems());
        cart.setCartItems(cartItems);
        cart.setTotalPrice(totalPrice);
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
