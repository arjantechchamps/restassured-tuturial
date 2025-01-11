package io.techchamps.restbackend.services;

import io.techchamps.restbackend.entity.User;
import io.techchamps.restbackend.exception.NotFoundException;
import io.techchamps.restbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUser {

    UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        // Get the current authenticated user's username
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Find and return the user based on the username
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }


    @Override
    public Optional<User> findByEmail(String email) {

        return userRepository.findByEmail(email);
    }


    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(int id) {

        userRepository.deleteById(id);
    }
}
