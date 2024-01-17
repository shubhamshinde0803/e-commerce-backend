package com.srs.service;

import com.srs.exception.ProductException;
import com.srs.model.Cart;
import com.srs.model.User;
import com.srs.repository.CartRepository;
import com.srs.request.AddItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private ProductService productService;



    @Override
    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    @Override
    public String addCartItem(Long userId, AddItemRequest req) throws ProductException {
        Cart cart = cartRepository.findByUserId(userId);

        return null;
    }

    @Override
    public Cart findUserCart(Long userId) {
        return null;
    }
}
