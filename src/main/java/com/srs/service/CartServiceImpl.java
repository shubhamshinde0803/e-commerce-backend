package com.srs.service;

import com.srs.exception.CartException;
import com.srs.exception.ProductException;
import com.srs.model.Cart;
import com.srs.model.CartItem;
import com.srs.model.Product;
import com.srs.model.User;
import com.srs.repository.CartItemRepository;
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
        Cart cart = cartRepository.findByUserId(userId) ;
//        if (cart == null){
//            cart = new Cart();
//        }
        Product product = productService.findProductById(req.getProductId());

        CartItem isPresent = cartItemService.isCartItemExist(cart, product, req.getSize(), userId);

        if(isPresent == null){
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(cart);
            cartItem.setQuantity(req.getQuantity());
            cartItem.setUserId(userId);

            int price = req.getQuantity()*product.getDiscountedPrice();
            cartItem.setPrice(price);
            cartItem.setSize(req.getSize());

            CartItem createdCartItem = cartItemService.createCartItem(cartItem);
            cart.getCartItems().add(createdCartItem);
        }

        return "Item added to cart";
    }

    @Override
    public Cart findUserCart(Long userId) throws CartException {
        Cart cart = cartRepository.findByUserId(userId);
        int totalPrice = 0;
        int totalDiscountedPrice = 0;
        int totalItem = 0;

        if(cart != null) {

            for (CartItem cartItem : cart.getCartItems()) {
                totalPrice = totalPrice + cartItem.getPrice();
                totalDiscountedPrice = totalDiscountedPrice + cartItem.getDiscountedPrice();
                totalItem = totalItem + cartItem.getQuantity();
            }
            cart.setTotalDiscountedPrice(totalDiscountedPrice);
            cart.setTotalItem(totalItem);
            cart.setTotalPrice(totalPrice);
            cart.setDiscount(totalPrice - totalDiscountedPrice);


            return cartRepository.save(cart);
        } else {
            throw new CartException("User does not have any item in cart");
        }
    }
}
