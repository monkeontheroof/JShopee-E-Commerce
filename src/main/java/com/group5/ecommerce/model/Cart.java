package com.group5.ecommerce.model;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import javax.persistence.CascadeType;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    private double totalPrice;

    private int totalItem;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public void setId(Long id) {
        this.id = id;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public void setTotalPrice() {
        this.totalPrice = getCartItems().stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
