package com.pedruuv.estore.service;

import com.pedruuv.estore.model.User;

public interface EmailVerificationService {
    void scheduleEmailConfirmation(User user);
}
