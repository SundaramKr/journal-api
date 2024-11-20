package com.example.Entries.controller;

import com.example.Entries.entity.Entry;
import com.example.Entries.entity.User;
import com.example.Entries.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        if (allUsers != null && !allUsers.isEmpty()) {
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No entries found in the collection", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/make-admin/{userName}")
    public ResponseEntity<?> makeUserAdmin(@PathVariable String userName) {
        User user = userService.findByUserName(userName);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            List<String> roles = user.getRoles();
            roles.add("ADMIN");
            user.setRoles(roles);
            userService.saveEntry(user);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
