package com.csye6225.neu.user.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import com.csye6225.neu.dto.User;
import com.csye6225.neu.repository.UserRepository;
import com.csye6225.neu.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CreateUserTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateUser() throws Exception {
        User user = new User();
        user.setFirstName("Mrinal");
        user.setLastName("Sharma");
        user.setEmail("mrinalsharma20206@gmail.com");
        user.setPassword("@3IwbvsA@3");
        userRepository.save(user);
        User userFound = userRepository.findByEmail(user.getEmail());
        assertNotNull(userFound);
        assertEquals("Username is equal", user.getEmail(), userFound.getEmail());
        userRepository.delete(user);
    }

}
