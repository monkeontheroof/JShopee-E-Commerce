package com.group5.ecommerce.service;

import com.group5.ecommerce.model.CustomUserDetail;
import com.group5.ecommerce.model.User;
import com.group5.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        user.orElseThrow(() -> new UsernameNotFoundException("Could not find user"));
        if(!user.get().isLocked()){
            return user.map(CustomUserDetail::new).get();
        }else {
            throw new LockedException("Tài khoản đã bị khóa do nhập sai thông tin quá 3 lần.");
        }
    }
}
