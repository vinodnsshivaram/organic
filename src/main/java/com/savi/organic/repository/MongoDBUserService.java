package com.savi.organic.repository;

import com.savi.organic.data.User;
import com.savi.organic.exceptions.MongoDBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Repository
public class MongoDBUserService implements UserRepository {
    private final MongoOperations operations;

    @Autowired
    public MongoDBUserService(MongoOperations operations) {
        Assert.notNull(operations, "MongoOperations cannot be null");
        this.operations = operations;
    }

    @Override
    public User save(User user) {
        operations.save(user);
        return user;
    }

    @Override
    public User findByUsername(String username) {
        if (StringUtils.isEmpty(username))
            throw new MongoDBException("User Name cannot be empty");

        Query query = new Query();
        Criteria userNameCriteria = Criteria.where("username").is(username);
        query.addCriteria(userNameCriteria);
        return operations.findOne(query, User.class);
    }

    @Override
    public User findByEmail(String email) {
        if (StringUtils.isEmpty(email))
            throw new MongoDBException("Email cannot be empty");

        Query query = new Query();
        Criteria userNameCriteria = Criteria.where("email").is(email);
        query.addCriteria(userNameCriteria);
        return operations.findOne(query, User.class);
    }
}
