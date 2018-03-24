package com.savi.organic.controller;

import com.savi.organic.data.Token;
import com.savi.organic.data.User;
import com.savi.organic.exceptions.*;
import com.savi.organic.repository.MongoDBUserService;
import com.savi.organic.service.TokenGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MongoDBUserService userRepository;
    @Autowired
    private TokenGeneratorService tokenService;



    @RequestMapping(value = "/signup", method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity singup(@RequestBody User user){
        if (StringUtils.isEmpty(user.getUsername()))
            throw new BadRequestException("User name is a required field");
        if (StringUtils.isEmpty(user.getPassword()))
            throw new BadRequestException("Password is a required field");
        if (StringUtils.isEmpty(user.getEmail()))
            throw new BadRequestException("Email is a required field");
        if (!StringUtils.isEmpty(user.getAuthGroup()))
            throw new BadRequestException("Cannot set AuthGroup");

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        User newUser = new User(user.getUsername(), encodedPassword, user.getEmail());
        try {
            user = userRepository.save(newUser);
            user.setPassword(null);
        } catch (DuplicateKeyException e){
            throw new DuplicateRecordException("An user already exists with username : " + user.getUsername());
        } catch(Exception e) {
           throw new MongoDBException("Unknown Mongodb Exception : " + e);
        }

        return new ResponseEntity(newUser, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/signin", method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity signin(@RequestBody User user) {
        if (StringUtils.isEmpty(user.getUsername()))
            throw new BadRequestException("User name is a required field");
        if (StringUtils.isEmpty(user.getPassword()))
            throw new BadRequestException("Password is a required field");

        User userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB == null)
            throw new ItemNotFoundException("Not User was found with username : " + user.getUsername());

        if (!passwordEncoder.matches(user.getPassword(), userFromDB.getPassword()))
            throw new UnAuthorizedException("Password missmatch.");

        Token token = new Token(tokenService.generateJWT(user), "success");

        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
