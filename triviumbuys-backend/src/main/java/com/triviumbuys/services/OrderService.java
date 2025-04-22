package com.triviumbuys.services;

import com.triviumbuys.dto.*;
import com.triviumbuys.entity.*;
import com.triviumbuys.kafka.OrderKafkaProducer;
import com.triviumbuys.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DeliveryPersonRepository deliveryPersonRepository;

    @Autowired
    private OrderKafkaProducer orderKafkaProducer;

    private final AtomicInteger counter = new AtomicInteger(0);

    // ✅ Create Order
    @Transactional
    public OrderResponseDto createOrder(Long userId, CreateOrderRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<CartItem> cartItems = cartItemRepository.findAllById(request.getCartItemIds());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double totalAmount = 0.0;

        Order order = new Order();
        order.setCustomerId(user.getId());
        order.setOrderDate(LocalDateTime.now());
        order.setCreatedDate(LocalDateTime.now());
        order.setAddress(request.getAddress());
        order.setStatus(OrderStatus.PLACED);
        order.setTotalAmount(0.0); // temporary
        order = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + cartItem.getProductId()));

            int quantity = cartItem.getQuantity();
            if (product.getStock() < quantity) {
                throw new RuntimeException("Product '" + product.getName() + "' does not have enough stock.");
            }

            product.setStock(product.getStock() - quantity);
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(quantity);
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);

            totalAmount += (product.getPrice() * quantity);
        }

        orderItemRepository.saveAll(orderItems);

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);

        String message = "OrderId:" + order.getId() + ",UserId:" + user.getId() + ",Amount:" + totalAmount + ",Address:" + order.getAddress();
        orderKafkaProducer.sendOrder(message);

        assignDeliveryPerson(order.getId());

        OrderResponseDto response = new OrderResponseDto();
        response.setOrderId(order.getId());
        response.setMessage("Order placed successfully!");
        response.setTotalAmount(totalAmount);
        return response;
    }

    @Transactional
    public void assignDeliveryPerson(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

            if (order.getDeliveryPersonId() != null) {
                return;
            }

            List<DeliveryPerson> deliveryPeople = deliveryPersonRepository.findAll();
            if (deliveryPeople.isEmpty()) {
                return;
            }

            int index = counter.getAndIncrement() % deliveryPeople.size();
            DeliveryPerson deliveryPerson = deliveryPeople.get(index);

            order.setDeliveryPersonId(deliveryPerson.getId());
            orderRepository.save(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<OrderItemResponseDto> mapOrderItems(List<OrderItem> orderItems) {
        List<OrderItemResponseDto> itemDtos = new ArrayList<>();

        for (OrderItem item : orderItems) {
            if (item.getProductId() == null) continue;
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                OrderItemResponseDto itemDto = new OrderItemResponseDto();
                itemDto.setProductId(product.getId());
                itemDto.setProductName(product.getName());
                itemDto.setProductImage(product.getImageUrl());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setPrice(item.getPrice());
                itemDtos.add(itemDto);
            }
        }
        return itemDtos;
    }

    // ✅ Get Customer Orders
    public List<CustomerOrderDto> getOrdersByCustomer(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        List<CustomerOrderDto> customerOrders = new ArrayList<>();

        for (Order order : orders) {
            CustomerOrderDto dto = new CustomerOrderDto();
            dto.setId(order.getId());
            dto.setOrderDate(order.getOrderDate());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setStatus(order.getStatus().toString());
            dto.setItems(mapOrderItems(order.getOrderItems()));
            customerOrders.add(dto);
        }
        return customerOrders;
    }

    public Order getOrderDetails(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<RecentOrderDto> getAllOrdersForAdmin() {
        List<Order> orders = orderRepository.findAll();
        List<RecentOrderDto> responseDtos = new ArrayList<>();

        for (Order order : orders) {
            RecentOrderDto dto = new RecentOrderDto();
            dto.setOrderId(order.getId());
            Optional<User> optionalUser = userRepository.findById(order.getCustomerId());
            dto.setCustomerName(optionalUser.map(User::getName).orElse("Unknown"));
            dto.setStatus(order.getStatus().toString());
            dto.setDate(order.getOrderDate() != null ? order.getOrderDate().toString() : "");
            dto.setTotal(order.getTotalAmount());
            dto.setItems(mapOrderItems(order.getOrderItems()));
            responseDtos.add(dto);
        }
        return responseDtos;
    }

    public String updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        order.setStatus(status);
        orderRepository.save(order);
        return "Order status updated successfully!";
    }

    @Transactional
    public void assignAllUnassignedOrders() {
        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            if (order.getDeliveryPersonId() == null) {
                assignDeliveryPerson(order.getId());
            }
        }
    }

    // ✅ Delivery Person - Get Assigned Orders (Use CustomerOrderDto)
 // 🆕 Correct version
    public List<DeliveryOrderDto> getAssignedOrdersForDelivery(Long deliveryPersonId) {
        List<Order> orders = orderRepository.findByDeliveryPersonId(deliveryPersonId);
        List<DeliveryOrderDto> deliveryOrders = new ArrayList<>();

        for (Order order : orders) {
            DeliveryOrderDto dto = new DeliveryOrderDto();
            dto.setId(order.getId());
            dto.setOrderDate(order.getOrderDate());
            dto.setAddress(order.getAddress());
            dto.setStatus(order.getStatus().toString());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setItems(mapOrderItems(order.getOrderItems())); // items from OrderItems
            deliveryOrders.add(dto);
        }

        return deliveryOrders;
    }

}