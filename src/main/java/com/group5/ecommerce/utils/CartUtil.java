package com.group5.ecommerce.utils;

import com.group5.ecommerce.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CartUtil {
    public static List<Product> cart;
    public static Integer COUNT = 0;
    static {
        cart = new ArrayList<>();
        ++COUNT;
    }
}
