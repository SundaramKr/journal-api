package com.example.Entries.service;

import com.example.Entries.entity.User;
import com.example.Entries.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByUserName() {
        assertNotNull(userRepository.findByUserName("Ram"));
    }

    @Test
    public void testNotEmptyGetAllUsersList() {
        assertNotEquals(0, userRepository.findAll().size());
    }

    @Test
    public void testNotEmptyUserEntries() {
        User user = userRepository.findByUserName("Ram");
        assertFalse(user.getEntries().isEmpty());
    }

    @Test
    public void testPasswordBCryptEncoding() {
        User user = userRepository.findByUserName("Ram");
        //Regex for BCrypt Encoding
        Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");
        assertTrue(BCRYPT_PATTERN.matcher(user.getPassword()).matches());
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "Ram",
            "Shyam"
    })
    public void parameterizedTestFindByUserName(String userName) {
        assertNotNull(userRepository.findByUserName(userName));
    }
}
