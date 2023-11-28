package com.group5.ecommerce.service;

import com.group5.ecommerce.model.User;
import com.group5.ecommerce.model.UserStore;
import com.group5.ecommerce.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    public List<UserStore> getAllStores(){
        return storeRepository.findAll();
    }

    public UserStore getStoreById(Long id){
        return storeRepository.findById(id).orElse(null);
    }

    public UserStore getStoreByUserId(Long userId){
        return storeRepository.findByUserId(userId);
    }

    public void addStore(UserStore store){
        storeRepository.save(store);
    }

    public void removeStoreById(Long id){
        storeRepository.deleteById(id);
    }


}
