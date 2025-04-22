package com.triviumbuys.services;

import com.triviumbuys.entity.DeliveryPerson;
import com.triviumbuys.repository.DeliveryPersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DeliveryPersonService {

    @Autowired
    private DeliveryPersonRepository deliveryPersonRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DeliveryPerson registerDeliveryPerson(DeliveryPerson deliveryPerson) {
        // 🔒 Encrypt password
        deliveryPerson.setPassword(passwordEncoder.encode(deliveryPerson.getPassword()));
        return deliveryPersonRepository.save(deliveryPerson);
    }

    public DeliveryPerson getByUsername(String username) {
        return deliveryPersonRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Delivery Person not found"));
    }
}
