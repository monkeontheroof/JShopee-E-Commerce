package com.group5.ecommerce.service;

import com.group5.ecommerce.model.*;
import com.group5.ecommerce.repository.RoleRepository;
import com.group5.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public Page<User> getAllUsers(Pageable pageable){
        return userRepository.findAll(pageable);
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email){
        if(isValidEmail(email)){
            Optional<User> user = userRepository.findByEmail(email);
            if(user.isPresent())
                return user.get();
        }
        return null;
    }

    private boolean isValidEmail(String email) {
        //check if the email is valid using regex pattern
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$") && !email.isEmpty();
    }

    public void createNewUser(RegistrationForm registerUser){
        if(registerUser.getPassword().equals(registerUser.getConfirmPassword())){
            List<Role> roles = new ArrayList<>();
            roles.add(roleRepository.findByName("ROLE_USER"));
            User user = User.builder()
                    .firstName(registerUser.getFirstName())
                    .lastName(registerUser.getLastName())
                    .phone(registerUser.getPhone())
                    .password(bCryptPasswordEncoder.encode(registerUser.getPassword()))
                    .email(registerUser.getEmail())
                    .roles(roles)
                    .cart(new Cart())
                    .build();
            userRepository.save(user);
        }
    }

    public void save(User user){
        userRepository.save(user);
    }

    public void addUser(User user){
        userRepository.save(user);
    }

    public void updateUser(User user){
        userRepository.save(user);
    }

    public void removeUserById(Long id){
        userRepository.deleteById(id);
    }
}
