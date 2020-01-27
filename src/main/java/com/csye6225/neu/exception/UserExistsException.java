package com.csye6225.neu.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserExistsException extends RuntimeException {


    public UserExistsException() {
        super();
    }

    public UserExistsException(String user) {
        super(String.format("Invalid User:%s, User exception !!", user));
    }

    public UserExistsException(Throwable cause) {
        super(cause);
    }
}