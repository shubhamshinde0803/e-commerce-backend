package com.srs.service;

import com.srs.exception.OrderException;
import com.srs.exception.UserException;
import com.srs.model.*;
import com.srs.repository.*;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;
//    @Autowired
//    private CartRepository cartRepository;
//    @Autowired
//    private CartItemService cartItemService;
//    @Autowired
//    private ProductService productService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public Order createOrder(User user, Address shippingAddress) throws UserException {
        try {
            System.out.println("Creating order for user: " + user.getEmail());
            shippingAddress.setUser(user);
            Address address = addressRepository.save(shippingAddress);
            user.getAddress().add(address);
            userRepository.save(user);

            Cart cart = cartService.findUserCart(user.getId());
            List<OrderItem> orderItems = new ArrayList<>();

            for (CartItem item : cart.getCartItems()) {
                OrderItem orderItem = new OrderItem();

                orderItem.setPrice(item.getPrice());
                orderItem.setProduct(item.getProduct());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setSize(item.getSize());
                orderItem.setUserId(item.getUserId());
                orderItem.setDiscountedPrice(item.getDiscountedPrice());

                OrderItem createdOrderItem = orderItemRepository.save(orderItem);

                orderItems.add(createdOrderItem);
            }

            Order createdOrder = new Order();
            createdOrder.setUser(user);
            createdOrder.setOrderItems(orderItems);
            createdOrder.setTotalPrice(cart.getTotalPrice());
            createdOrder.setTotalDiscountedPrice(cart.getTotalDiscountedPrice());
            createdOrder.setDiscount(cart.getDiscount());
            createdOrder.setTotalItems(cart.getTotalItem());
            createdOrder.setShippingAddress(address);
            createdOrder.setOrderDate(LocalDateTime.now());
            createdOrder.setOrderStatus("PENDING");
//        createdOrder.setPaymentDetails().setStatus("PENDING");
            createdOrder.setCreatedAt(LocalDateTime.now());

            Order savedOrder = orderRepository.save(createdOrder);

            for (OrderItem item : orderItems) {
                item.setOrder(savedOrder);
                orderItemRepository.save(item);
            }
            System.out.println("order created successfully");
            return savedOrder;
        }catch (Exception e){
            e.printStackTrace();
            throw new UserException("Error creating order for user: " + user.getEmail());
        }
    }

    @Override
    public Order placedOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        order.setOrderStatus("PLACED");
//        order.getPaymentDetails().setStatus("COMPLETED");
        return order;
    }

    @Override
    public Order confirmedOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        order.setOrderStatus("CONFIRMED");
        return orderRepository.save(order);
    }

    @Override
    public Order shippedOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        order.setOrderStatus("SHIPPED");
        return orderRepository.save(order);
    }

    @Override
    public Order deliveredOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        order.setOrderStatus("DELIVERED");
        return orderRepository.save(order);
    }

    @Override
    public Order cancelledOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        order.setOrderStatus("CANCELLED");
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public void deleteOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        orderRepository.delete(order);
    }

    @Override
    public Order findOrderById(Long orderId) throws OrderException {
        Optional<Order> opt = orderRepository.findById(orderId);
        if(opt.isPresent()){
            return opt.get();
        }

        throw new OrderException("order does not exist with id: " + orderId);
    }

    @Override
    public List<Order> usersOrderHistory(Long userId) {
        return orderRepository.getUserOrders(userId);
    }
}
