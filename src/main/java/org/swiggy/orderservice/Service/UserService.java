package org.swiggy.orderservice.Service;


import org.swiggy.orderservice.Repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.swiggy.orderservice.DTO.UserRequestDTO;
import org.swiggy.orderservice.Exception.ResourceAlreadyExistsException;
import org.swiggy.orderservice.Model.User;

import java.util.Objects;
import java.util.Optional;


@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserRequestDTO userRequest) {
        try {
            String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
            User user = new User(userRequest.getUsername(), encodedPassword, userRequest.getRole());
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceAlreadyExistsException("User already exists");
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public boolean validateUser(Long userId, String username) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            return false;
        }
        if (!Objects.equals(user.get().getUsername(), username)) {
            return false;
        }
        return true;
    }
}
