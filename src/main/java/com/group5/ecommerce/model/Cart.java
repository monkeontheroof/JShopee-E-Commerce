package com.group5.ecommerce.model;

import lombok.Data;

import javax.persistence.*;
import javax.persistence.CascadeType;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart")
    private List<CartItem> cartItems;

    private double totalPrice;

    private int totalItem;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
