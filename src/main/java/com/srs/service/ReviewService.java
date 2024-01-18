package com.srs.service;

import com.srs.exception.ProductException;
import com.srs.model.Review;
import com.srs.model.User;
import com.srs.request.ReviewRequest;

import java.util.List;

public interface ReviewService {

    public Review createReview(ReviewRequest req, User user) throws ProductException;

    public List<Review> getAllReview(Long productId);


}
