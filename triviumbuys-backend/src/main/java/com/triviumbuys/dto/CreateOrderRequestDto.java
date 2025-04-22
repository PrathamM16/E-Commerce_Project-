package com.triviumbuys.dto;

import java.util.List;

public class CreateOrderRequestDto {

    private List<Long> cartItemIds;
    private String address;

    // --- Constructors ---
    public CreateOrderRequestDto() {
    }

    public CreateOrderRequestDto(List<Long> cartItemIds, String address) {
        this.cartItemIds = cartItemIds;
        this.address = address;
    }

    // --- Getters and Setters ---
    public List<Long> getCartItemIds() {
        return cartItemIds;
    }

    public void setCartItemIds(List<Long> cartItemIds) {
        this.cartItemIds = cartItemIds;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
