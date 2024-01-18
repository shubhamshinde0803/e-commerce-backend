package com.srs.service;

import com.srs.exception.ProductException;
import com.srs.model.Rating;
import com.srs.model.User;
import com.srs.request.RatingRequest;

import java.util.List;

public interface RatingService {
    public Rating createRating(RatingRequest req, User user) throws ProductException;

    public List<Rating> getProductsRating(Long productId);
}
