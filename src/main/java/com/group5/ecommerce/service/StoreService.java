package com.group5.ecommerce.service;

import com.group5.ecommerce.model.Role;
import com.group5.ecommerce.model.User;
import com.group5.ecommerce.model.UserStore;
import com.group5.ecommerce.repository.RoleRepository;
import com.group5.ecommerce.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    public Page<UserStore> getAllStores(Pageable pageable){
        return storeRepository.findAll(pageable);
    }

    public UserStore getStoreById(Long id){
        return storeRepository.findById(id).orElse(null);
    }

    public UserStore getStoreByUserId(Long userId){
        return storeRepository.findByUserId(userId);
    }

    public void addStore(UserStore store, Long userId){
        User user = userService.getUserById(userId);
        List<Role> roles = user.getRoles();
        roles.add(roleRepository.findByName("ROLE_STORE_ADMIN"));
        store.setLocked(false);
        store.setDate(LocalDate.now());
        user.setStore(store);
        userService.save(user);
    }

    public void removeStoreById(Long id){
        storeRepository.deleteById(id);
    }

    public void lockStore(Long storeId){
        UserStore store = getStoreById(storeId);
        store.setLocked(true);
        storeRepository.save(store);
    }

    public void unlockStore(Long storeId){
        UserStore store = getStoreById(storeId);
        store.setLocked(false);
        storeRepository.save(store);
    }
}
