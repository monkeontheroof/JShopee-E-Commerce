package com.group5.ecommerce.utils;

import com.group5.ecommerce.model.CustomUserDetail;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    public static CustomUserDetail getPrincipal(){
        return (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
