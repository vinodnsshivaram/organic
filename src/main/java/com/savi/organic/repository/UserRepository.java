package com.savi.organic.repository;

import com.savi.organic.data.User;
import org.springframework.data.repository.Repository;

import java.util.UUID;

public interface UserRepository extends Repository<User, UUID> {
    User save(User user);
    public User findByUsername(String username);
    public User findByEmail(String email);
}
