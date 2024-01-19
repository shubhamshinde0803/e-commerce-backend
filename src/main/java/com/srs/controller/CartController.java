package com.srs.controller;

import com.srs.exception.CartException;
import com.srs.exception.ProductException;
import com.srs.exception.UserException;
import com.srs.model.Cart;
import com.srs.model.User;
import com.srs.request.AddItemRequest;
import com.srs.response.ApiResponse;
import com.srs.service.CartService;
import com.srs.service.UserService;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;

    @GetMapping("/")
    public ResponseEntity<Cart> findUserCart(@RequestHeader("Authorization") String jwt)throws UserException, CartException {
        User user = userService.findUserProfileByJwt(jwt);
        Cart cart = cartService.findUserCart(user.getId());
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @PutMapping("/add")
    public ResponseEntity<ApiResponse> addItemToCart(@RequestBody AddItemRequest req, @RequestHeader("Authorization") String jwt) throws UserException, ProductException{
        User user = userService.findUserProfileByJwt(jwt);
        cartService.addCartItem(user.getId(), req);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("item added to cart");
        apiResponse.setStatus(true);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
