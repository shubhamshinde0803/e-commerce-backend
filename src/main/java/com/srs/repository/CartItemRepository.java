package com.srs.repository;

import com.srs.model.Cart;
import com.srs.model.CartItem;
import com.srs.model.Product;
import com.srs.service.CartItemService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart = :cart AND ci.product = :product AND ci.size = :size AND ci.userId = :userId")
public CartItem isCartItemExist(
            @Param("cart") Cart cart,
            @Param("product")Product product,
            @Param("size") String size,
            @Param("userId") Long userId
            );
}
