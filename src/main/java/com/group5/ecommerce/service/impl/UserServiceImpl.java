package com.group5.ecommerce.service.impl;

import com.group5.ecommerce.model.*;
import com.group5.ecommerce.repository.RoleRepository;
import com.group5.ecommerce.repository.UserRepository;
import com.group5.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id){
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUserByEmail(String email){
        if(isValidEmail(email)){
            Optional<User> user = userRepository.findByEmail(email);
            if(user.isPresent())
                return user.get();
        }
        return null;
    }

    @Override
    public void createNewUser(RegistrationForm registerUser){
        if(registerUser.getPassword().equals(registerUser.getConfirmPassword())){
            List<Role> roles = new ArrayList<>();
            roles.add(roleRepository.findByName("ROLE_USER"));
            User user = User.builder() //TODO: Builder
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

    @Override
    public void saveUser(User user){
        userRepository.save(user);
    }

    @Override
    public void removeUserById(Long id){
        userRepository.deleteById(id);
    }

    public void updateUser(User user){
        userRepository.save(user);
    }

    private boolean isValidEmail(String email) {
        //check if the email is valid using regex pattern
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$") && !email.isEmpty();
    }
}
