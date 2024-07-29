package com.pedruuv.estore.data;

import com.pedruuv.estore.model.User;

public interface UserRepository {
    boolean save(User user);
}
