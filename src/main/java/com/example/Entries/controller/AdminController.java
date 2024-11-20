package com.example.Entries.controller;

import com.example.Entries.entity.User;
import com.example.Entries.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody User user) {
        userService.saveAdminPassEntry(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //Create User using PublicUserController.
    //Pass the userName as a PathVariable to give it the "ADMIN" role.
    @PostMapping("/set-admin/{userName}")
    public ResponseEntity<?> setUserAdmin(@PathVariable String userName) {
        User user = userService.findByUserName(userName);
        if (user == null) {
            return new ResponseEntity<>("Username not found", HttpStatus.NOT_FOUND);
        } else {
            List<String> roles = user.getRoles();
            if(roles.contains("ADMIN")){
                return new ResponseEntity<>("User is already an Admin", HttpStatus.BAD_REQUEST);
            }
            roles.add("ADMIN");
            user.setRoles(roles);
            userService.saveEntry(user);
            return new ResponseEntity<>("Added ADMIN role", HttpStatus.OK);
        }
    }

    @GetMapping("/get-user/{userName}")
    public ResponseEntity<?> getByUsername(@PathVariable String userName) {
        User user = userService.findByUserName(userName);
        if (user == null) {
            return new ResponseEntity<>("Username not found", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }

    @DeleteMapping("/delete-user/{userName}")
    public ResponseEntity<?> deleteByUsername(@PathVariable String userName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authUserName = authentication.getName();
        User user = userService.findByUserName(userName);
        if (authUserName.equals(userName)) {
            return new ResponseEntity<>("Entered userName and Admin userName are the same", HttpStatus.BAD_REQUEST);
        } else if (user == null) {
            return new ResponseEntity<>("Username not found", HttpStatus.NOT_FOUND);
        } else {
            ObjectId userId = user.getId();
            userService.removeEntryById(userId);
            return new ResponseEntity<>("Deleted User:\n" + user, HttpStatus.OK);
        }
    }

    @PutMapping("/update-user/{userName}")
    public ResponseEntity<?> updateByUsername(@PathVariable String userName, @RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authUserName = authentication.getName();
        User userInDb = userService.findByUserName(userName);
        if (userInDb == null) {
            return new ResponseEntity<>("Username not found", HttpStatus.NOT_FOUND);
        }
        if (userInDb.getRoles().contains("ADMIN")) {
            if (userName.equals(authUserName)) {
                userInDb.setUserName(user.getUserName());
                userInDb.setPassword(user.getPassword());
                userService.saveAdminPassEntry(userInDb);
                return new ResponseEntity<>("Updated the current Admin",HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Can't Update a different Admin", HttpStatus.BAD_REQUEST);
            }
        } else {
            userInDb.setUserName(user.getUserName());
            userInDb.setPassword(user.getPassword());
            userService.saveUserPassEntry(userInDb);
            return new ResponseEntity<>("Updated the user "+userName,HttpStatus.OK);
        }
    }
}
