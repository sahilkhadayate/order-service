package org.swiggy.order.Controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swiggy.order.DTO.UserRequest;
import org.swiggy.order.Service.UserService.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest userRequest){
        System.out.println("====================================================");
        System.out.println("Creating user");
        System.out.println("====================================================");
        userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Created user successfully");
    }

}
