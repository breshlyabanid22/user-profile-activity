package com.csit321.userprofile.service;

import com.csit321.userprofile.model.User;
import com.csit321.userprofile.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    public ResponseEntity<String> register(User user){

        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            return new ResponseEntity<>("Username already exist", HttpStatus.CONFLICT);
        }else if(userRepository.findByEmail(user.getEmail()).isPresent()){
            return new ResponseEntity<>("Email already exist", HttpStatus.CONFLICT);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        userRepository.save(user);
        return new ResponseEntity<>("Registered Successfully", HttpStatus.CREATED);
    }
    public ResponseEntity<String> login(String username, String password, HttpServletRequest request){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            if(authentication != null){
                SecurityContextHolder.getContext().setAuthentication(authentication);
                HttpSession session = request.getSession(true);
                session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
                return new ResponseEntity<>("Login successful", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Invalid Username or password", HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e){
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    public ResponseEntity<String> updateProfile(User updatedUserData, Long userId){
        Optional<User> userOptional = userRepository.findById(userId);

        if(userOptional.isEmpty()){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User existingUserData = userOptional.get();
        if(userRepository.findByEmail(updatedUserData.getEmail()).isPresent() && updatedUserData.getEmail() != existingUserData.getEmail()){
            return new ResponseEntity<>("Email already exist", HttpStatus.CONFLICT);
        }else if(userRepository.findByUsername(updatedUserData.getUsername()).isPresent() && updatedUserData.getUsername() != existingUserData.getUsername()){
            return new ResponseEntity<>("Username already exist", HttpStatus.CONFLICT);
        }
        existingUserData.setUsername(updatedUserData.getUsername() != null ? updatedUserData.getUsername(): existingUserData.getUsername());
        if(updatedUserData.getPassword() != null){
            existingUserData.setPassword(passwordEncoder.encode(updatedUserData.getPassword()));
        }else{
            existingUserData.setPassword(passwordEncoder.encode(existingUserData.getPassword()));
        }
        existingUserData.setFirstName(updatedUserData.getFirstName() != null ? updatedUserData.getFirstName() : existingUserData.getFirstName());
        existingUserData.setLastName(updatedUserData.getLastName() != null ? updatedUserData.getLastName() : existingUserData.getLastName());
        existingUserData.setEmail(updatedUserData.getEmail() != null ? updatedUserData.getEmail() : existingUserData.getEmail());
        existingUserData.setAge(updatedUserData.getAge() != 0 ? updatedUserData.getAge() : existingUserData.getAge());
        existingUserData.setGender(updatedUserData.getGender() != null ? updatedUserData.getGender() : existingUserData.getGender());
        existingUserData.setRole(updatedUserData.getRole() != null ? updatedUserData.getRole() : existingUserData.getRole());
        userRepository.save(existingUserData);
        return new ResponseEntity<>("User profile has been updated!", HttpStatus.OK);
    }

    public ResponseEntity<String> deactivateAccount(User user){
        user.setActive(false);
        userRepository.save(user);
        return new ResponseEntity<>("User account deactivated", HttpStatus.NOT_FOUND);
    }
}
