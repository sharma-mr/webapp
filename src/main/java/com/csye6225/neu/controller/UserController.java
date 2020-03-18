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
        statsd.incrementCounter("UserHttpPost");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ResponseEntity<User> userCreated = userService.createUser(user);
        stopWatch.stop();
        statsd.recordExecutionTime("UserHttpPost",stopWatch.getLastTaskTimeMillis());
        return userCreated;
    }

    @GetMapping("/v1/user/self")
    public ResponseEntity<User> getUser(@RequestHeader("authorization") String auth) {
        if (!auth.isEmpty()) {
            return userService.getUser(auth);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }

    @PutMapping("/v1/user/self")
    public ResponseEntity<User> updateUser(@RequestHeader("authorization") String auth, @Valid @RequestBody User user) {
        if (!auth.isEmpty()) {
            return userService.updateUser(auth, user);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}