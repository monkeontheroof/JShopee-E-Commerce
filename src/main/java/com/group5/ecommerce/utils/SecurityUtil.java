package com.group5.ecommerce.utils;

import com.group5.ecommerce.model.CustomUserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.security.Principal;
import java.util.Optional;

public class SecurityUtil {
    public static Optional<CustomUserDetail> getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetail) {
            return Optional.of((CustomUserDetail) authentication.getPrincipal());
        }
        return Optional.empty();
    }
}
