package com.group5.ecommerce.model;

import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;

@Data
@Entity
@Table(name="product")
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

    private Long quantity;

    @ManyToOne
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    private UserStore store;
}
