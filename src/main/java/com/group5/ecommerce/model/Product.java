package com.group5.ecommerce.model;

import lombok.*;
import org.springframework.format.annotation.NumberFormat;

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
    private UserStore store;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) && Objects.equals(name, product.name) && Objects.equals(category, product.category) && Objects.equals(price, product.price) && Objects.equals(description, product.description) && Objects.equals(imageName, product.imageName) && Objects.equals(quantity, product.quantity) && Objects.equals(store, product.store) && Objects.equals(orderItems, product.orderItems);
    }
}
