package com.group5.ecommerce.model;

import lombok.Data;

import javax.persistence.*;
import javax.persistence.CascadeType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart")
    private List<CartItem> cartItems = new ArrayList<>();

    private double totalPrice;

    private int totalItem;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return Double.compare(totalPrice, cart.totalPrice) == 0 && totalItem == cart.totalItem && Objects.equals(id, cart.id) && Objects.equals(cartItems, cart.cartItems) && Objects.equals(user, cart.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cartItems, totalPrice, totalItem, user);
    }
}
