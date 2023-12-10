package com.group5.ecommerce.service;

import com.group5.ecommerce.model.RegistrationForm;
import com.group5.ecommerce.model.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id);

    User getUserByEmail(String email);

    void saveUser(User user);

    void removeUserById(Long id);

    void createNewUser(RegistrationForm registerUser);
}
