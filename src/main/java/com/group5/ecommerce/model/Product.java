package com.group5.ecommerce.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.persistence.CascadeType;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="product")
@DynamicUpdate
public class Product {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private Double price;

    private String description;

    private String imageName;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    List<ProductImage> images;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    @Nullable
    private UserStore store;

//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
//    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<ProductDetail> details;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "product")
    private List<CartItem> cartItem;
}
