package com.triviumbuys.controller;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stripe")
public class StripeController {

    static {
        Stripe.apiKey = "your_secret_key_here"; // 🔥 Replace with your Stripe Secret Key (starts with sk_test_)
    }

    @PostMapping("/create-payment-intent")
    public Map<String, Object> createPaymentIntent(@RequestBody Map<String, Object> data) throws Exception {
        int amount = (int) data.get("amount"); // Example: 1000 = ₹10.00

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:4200/payment-success")  // 🔥 Change to your Angular success page
                .setCancelUrl("http://localhost:4200/payment-cancel")     // 🔥 Change to your Angular cancel page
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("inr")
                                                .setUnitAmount((long) amount) // amount in paise
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("TriviumBuys Cart Payment")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("id", session.getId());

        return responseData;
    }
}
