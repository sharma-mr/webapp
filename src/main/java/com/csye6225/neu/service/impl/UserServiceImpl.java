package com.csye6225.neu.service.impl;

import com.csye6225.neu.dto.User;
import com.csye6225.neu.exception.UserExistsException;
import com.csye6225.neu.exception.ValidationException;
import com.csye6225.neu.repository.UserRepository;
import com.csye6225.neu.service.UserService;
import com.csye6225.neu.utils.ValidationUtils;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.Base64;
import java.util.Date;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationUtils validator;

    @Autowired
    private StatsDClient statsd;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public ResponseEntity<User> createUser(User user) {
        if (!userExists(user.getEmail())) {
            if (validator.validate(user.getPassword())) {
                String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
                user.setPassword(hashedPassword);
                long start = System.currentTimeMillis();
                userRepository.save(user);
                logger.info("User created succesfully");
                logger.trace("User Id :- " + user.getId());
                long end = System.currentTimeMillis();
                long timeElapsed = end - start;
                logger.info("Time taken by save user database call is " + timeElapsed + "ms");
                statsd.recordExecutionTime("createUserSaveDBTime",timeElapsed);
                return new ResponseEntity<User>(user, HttpStatus.CREATED);
            } else {
                logger.error("Use a strong password");
                throw new ValidationException("Enter strong password");

            }
        } else {
            logger.error("User already exists");
            throw new UserExistsException(user.getEmail());
        }
    }

    @Override
    public boolean userExists(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null)
            return true;
        return false;
    }

    @Override
    public ResponseEntity<User> getUser(String auth) {
        String[] userInfo = new String(Base64.getDecoder().decode(auth.substring(6).getBytes())).split(":");
        logger.info("Calling find user database call");
        long start = System.currentTimeMillis();
        User user = userRepository.findByEmail(userInfo[0]);
        long end = System.currentTimeMillis();
        long timeElapsed = end - start;
        logger.info("Time taken by get user database call is " + timeElapsed + "ms");
        statsd.recordExecutionTime("getUserDBTime",timeElapsed);
        if (user == null) {
            logger.error("User does not exist");
            throw new UserExistsException("User does not exist");
        } else {
            if (new BCryptPasswordEncoder().matches(userInfo[1], user.getPassword())) {
                logger.info("User found");
                return new ResponseEntity(user, HttpStatus.OK);
            } else {
                logger.error("Invalid credentials");
                return new ResponseEntity("Invalid credentials", HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @Override
    public ResponseEntity<User> updateUser(String auth, User userRequest) {
        String[] userInfo = new String(Base64.getDecoder().decode(auth.substring(6).getBytes())).split(":");
        User user = userRepository.findByEmail(userInfo[0]);
        if (user == null) {
            throw new UserExistsException(userRequest.getEmail());
        } else {
            if (new BCryptPasswordEncoder().matches(userInfo[1], user.getPassword())) {
                if (user.getEmail().equals(userRequest.getEmail())) {
                    user.setFirstName(userRequest.getFirstName());
                    user.setLastName(userRequest.getLastName());
                    if (validator.validate(userRequest.getPassword())) {
                        String hashedPassword = BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt());
                        user.setPassword(hashedPassword);
                        user.setAccountUpdated(new Date());
                        logger.info("Calling update user database call");
                        long start = System.currentTimeMillis();
                        userRepository.save(user);
                        long end = System.currentTimeMillis();
                        long timeElapsed = end - start;
                        logger.info("Time taken by get update user database call is " + timeElapsed + "ms");
                        statsd.recordExecutionTime("updateUserDBTime",timeElapsed);
                        return new ResponseEntity(user, HttpStatus.NO_CONTENT);
                    } else {
                        logger.error("Enter Strong password");
                        throw new ValidationException("Enter Strong password");
                    }
                } else {
                    return new ResponseEntity("Bad Request", HttpStatus.BAD_REQUEST);
                }

            } else {
                logger.error("Invalid credentials");
                return new ResponseEntity("Invalid credentials", HttpStatus.UNAUTHORIZED);
            }
        }

    }
}
