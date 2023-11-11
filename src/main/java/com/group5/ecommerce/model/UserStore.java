package com.group5.ecommerce.model;

import lombok.Data;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "user_store")
public class UserStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(mappedBy = "store")
    private User user;

    @OneToMany(mappedBy = "store")
    private List<Order> orders;

    @OneToMany(mappedBy = "store")
    private List<Product> products;

    @OneToMany(mappedBy = "store")
    private List<Voucher> vouchers;
}
