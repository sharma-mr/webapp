package com.csye6225.neu.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ValidationUtils {

    private Pattern pattern;
    private Matcher matcher;

    private static final String REGEX_PASSWORD =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,15})";

    public ValidationUtils() {
        pattern = Pattern.compile(REGEX_PASSWORD);
    }

    public boolean validate(final String password) {

        matcher = pattern.matcher(password);
        return matcher.matches();

    }
}
