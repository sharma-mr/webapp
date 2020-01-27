package com.csye6225.neu.service;

import com.csye6225.neu.dto.User;
import org.springframework.http.ResponseEntity;


public interface UserService {

    public ResponseEntity<User> createUser(User user);

    public boolean userExists(String userName);

    public ResponseEntity<User> getUser(String auth);

    public ResponseEntity<User> updateUser(String auth, User user);

}
