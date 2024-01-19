package com.srs.service;

import com.srs.exception.CartException;
import com.srs.exception.ProductException;
import com.srs.model.Cart;
import com.srs.model.User;
import com.srs.request.AddItemRequest;

public interface CartService {

    public Cart createCart(User user);

    public String addCartItem(Long userId, AddItemRequest req) throws ProductException;

    public Cart findUserCart(Long userId) throws CartException;
}
