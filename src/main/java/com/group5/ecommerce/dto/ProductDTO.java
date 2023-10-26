package com.group5.ecommerce.dto;

import com.group5.ecommerce.model.Category;
import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

@Data
public class ProductDTO {

    private Long id;

    private String name;

    private Category category;

    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private Double price;

    private String description;

    private String imageName;

    private Long quantity;
}
