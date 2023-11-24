package com.group5.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="product_detail")
@DynamicUpdate
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(unique = true)
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name="product_id", referencedColumnName= "id")
    private Product product;
}
