package com.csye6225.neu.controller;

import com.csye6225.neu.dto.User;
import com.csye6225.neu.repository.UserRepository;
import com.csye6225.neu.service.UserService;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatsDClient statsd;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "/v1/user", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        logger.info("Calling create user API");
        statsd.incrementCounter("createUserApi");
        long start = System.currentTimeMillis();
        ResponseEntity<User> userCreated = userService.createUser(user);
        long end = System.currentTimeMillis();
        long timeElapsed = end - start;
        logger.info("Time taken by createUser API is " + timeElapsed + "ms");
        statsd.recordExecutionTime("createUserApiTime", timeElapsed);
        return userCreated;
    }

    @GetMapping("/v1/user/self")
    public ResponseEntity<User> getUser(@RequestHeader("authorization") String auth) {
        if (!auth.isEmpty()) {
            logger.info("Calling get user API");
            statsd.incrementCounter("getUserApi");
            long start = System.currentTimeMillis();
            ResponseEntity<User> userFound = userService.getUser(auth);
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            logger.info("Time taken by getUser API is " + timeElapsed + "ms");
            statsd.recordExecutionTime("getUserApiTime", timeElapsed);
            return userFound;
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }

    @PutMapping("/v1/user/self")
    public ResponseEntity<User> updateUser(@RequestHeader("authorization") String auth, @Valid @RequestBody User user) {
        if (!auth.isEmpty()) {
            logger.info("Calling update user API");
            statsd.incrementCounter("updateUserApi");
            long start = System.currentTimeMillis();
            ResponseEntity<User> updatedUser = userService.updateUser(auth, user);
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            logger.info("Time taken by updateUser API is " + timeElapsed + "ms");
            statsd.recordExecutionTime("updateUserApiTime", timeElapsed);
            return updatedUser;
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}