package com.example.Entries.controller;

import com.example.Entries.entity.User;
import com.example.Entries.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /* FOR ADMIN PURPOSES
    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }
    */

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        //Upon coming here, the user has already been authenticated. So there's no need to re-check the password.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User userInDb = userService.findByUserName(userName);
        //We can pass the new username or the new password in the body.
        //Using the PathVariable userName as reference, we can update the object.
        userInDb.setUserName(user.getUserName());
        userInDb.setPassword(user.getPassword());
        userService.saveUserPassEntry(userInDb);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User userInDb = userService.findByUserName(userName);
        userService.removeEntryById(userInDb.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
