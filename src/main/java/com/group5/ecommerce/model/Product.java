package com.group5.ecommerce.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.persistence.CascadeType;
import java.util.List;
import java.util.Objects;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private Double price;

    private String description;

    private String imageName;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    @Nullable
    private UserStore store;

//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
//    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews;
}
