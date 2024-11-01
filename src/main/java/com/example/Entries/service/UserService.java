package com.example.Entries.service;

import com.example.Entries.entity.User;
import com.example.Entries.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //GET
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //POST & PUT(update)
    public void saveEntry(User user) {
        userRepository.save(user);
    }

    public void saveUserPassEntry(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER"));
        userRepository.save(user);
    }

    //PathParam or GET by id
    public Optional<User> findEntryById(ObjectId myID) {
        return userRepository.findById(myID);
    }

    //DELETE
    public void removeEntryById(ObjectId myID) {
        userRepository.deleteById(myID);
    }

    //Update by Username
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }
}
