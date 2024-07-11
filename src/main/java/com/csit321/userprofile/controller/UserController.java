package com.csit321.userprofile.controller;

import com.csit321.userprofile.config.CustomUserDetails;
import com.csit321.userprofile.model.User;
import com.csit321.userprofile.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user){
        return userService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user, HttpServletRequest request){
        return userService.login(user.getUsername(), user.getPassword(), request);
    }
    @PutMapping("/account/update")
    public ResponseEntity<String> updateProfile(@RequestBody User updatedUserData, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Long userId = customUserDetails.getUserId();
        return userService.updateProfile(updatedUserData, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers(){
        return userService.getAllUsers();
    }

    @DeleteMapping("/account/deactivate")
    public ResponseEntity<String> deactivateAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        return userService.deactivateAccount(user);
    }

}
