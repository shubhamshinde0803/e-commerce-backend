package com.srs.service;

import com.srs.exception.CartItemException;
import com.srs.exception.UserException;
import com.srs.model.User;

public interface UserService {

    public User findUserById(Long userId) throws UserException, CartItemException;

    public User findUserProfileByJwt(String jwt) throws UserException;
}
