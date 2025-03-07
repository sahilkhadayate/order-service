package org.swiggy.order.Service.UserService;


import org.springframework.security.access.AccessDeniedException;
import org.swiggy.order.Exception.InvalidUserException;
import org.swiggy.order.Repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.swiggy.order.DTO.UserRequestDTO;
import org.swiggy.order.Exception.ResourceAlreadyExistsException;
import org.swiggy.order.Model.User;


@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserRequestDTO userRequest) {
        try {
            String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
            User user = new User(userRequest.getUsername(), encodedPassword);
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceAlreadyExistsException("User already exists");
        }
    }


    public User authenticateUser(String username, Long userId) {
            if (username==null || username.isEmpty()) {
                throw new InvalidUserException("invalid user");
            }
            User user = userRepository.getReferenceById(userId);
            if (!user.verifyUser(username)) {
                throw new AccessDeniedException("access denied");
            }
            return user;

    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceAlreadyExistsException("User not found"));
    }
}
