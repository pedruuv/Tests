package com.pedruuv.estore.service;

import com.pedruuv.estore.data.UserRepository;
import com.pedruuv.estore.data.UserRepositoryImpl;
import com.pedruuv.estore.model.User;

import java.util.UUID;

public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    EmailVerificationService emailVerificationService;

    public UserServiceImpl(UserRepository userRepository, EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.emailVerificationService = emailVerificationService;
    }

    @Override
    public User createUser(String firstName, String lastName, String email, String password, String repeatPassowrd) {

        if (firstName == null || firstName.trim().isEmpty()){
            throw new IllegalArgumentException("User's first name is empty");
        }
        if (lastName == null || lastName.trim().isEmpty()){
            throw new IllegalArgumentException("User's last name is empty");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")){
            throw new IllegalArgumentException("Email is invalid");
        }

        User user = new User(firstName, lastName, email, UUID.randomUUID().toString());
        boolean isUserCreated = false;

        try {
            userRepository.save(user);
        } catch (RuntimeException e){
            throw new UserServiceException(e.getMessage());
        }


        if (!isUserCreated) throw new UserServiceException("Could not create user");

        try{
            emailVerificationService.scheduleEmailConfirmation(user);
        } catch (RuntimeException e){
            throw new UserServiceException(e.getMessage());
        }

        return user;
    }
}
