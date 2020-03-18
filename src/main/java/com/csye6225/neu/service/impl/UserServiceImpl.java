package com.csye6225.neu.service.impl;

import com.csye6225.neu.dto.User;
import com.csye6225.neu.exception.UserExistsException;
import com.csye6225.neu.exception.ValidationException;
import com.csye6225.neu.repository.UserRepository;
import com.csye6225.neu.service.UserService;
import com.csye6225.neu.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationUtils validator;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public ResponseEntity<User> createUser(User user) {
        if (!userExists(user.getEmail())) {
            if (validator.validate(user.getPassword())) {
                String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
                user.setPassword(hashedPassword);
                userRepository.save(user);
                logger.info("User created succesfully");
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
        User user = userRepository.findByEmail(userInfo[0]);
        if (user == null) {
            throw new UserExistsException("User does not exist");
        } else {
            if (new BCryptPasswordEncoder().matches(userInfo[1], user.getPassword())) {
                return new ResponseEntity(user, HttpStatus.OK);
            } else {
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
                        userRepository.save(user);
                        return new ResponseEntity(user, HttpStatus.NO_CONTENT);
                    } else {
                        throw new ValidationException("Enter Strong password");
                    }
                } else {
                    return new ResponseEntity("Bad Request", HttpStatus.BAD_REQUEST);
                }

            } else {
                return new ResponseEntity("Invalid credentials", HttpStatus.UNAUTHORIZED);
            }
        }

    }
}
