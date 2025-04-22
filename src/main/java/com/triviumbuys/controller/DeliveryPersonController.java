package com.triviumbuys.controller;

import com.triviumbuys.entity.DeliveryPerson;
import com.triviumbuys.services.DeliveryPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryPersonController {

    @Autowired
    private DeliveryPersonService deliveryPersonService;

    @PostMapping("/register")
    public DeliveryPerson register(@RequestBody DeliveryPerson deliveryPerson) {
        return deliveryPersonService.registerDeliveryPerson(deliveryPerson);
    }
}
