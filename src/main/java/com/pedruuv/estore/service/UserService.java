package com.pedruuv.estore.service;

import com.pedruuv.estore.model.User;

public interface UserService {
    User createUser(String firstName, String lastName, String email, String password, String repeatPassowrd);

}
