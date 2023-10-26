package com.group5.ecommerce.utils;

import com.group5.ecommerce.dto.ProductDTO;

import java.util.ArrayList;
import java.util.List;

public class CartUtil {
    public static List<ProductDTO> cart;
    static {
        cart = new ArrayList<ProductDTO>();
    }
}
