package io.techchamps.restbackend.services;

import io.techchamps.restbackend.entity.User;

import java.util.List;
import java.util.Optional;

public interface IUser {
    List<User> getAllUsers();
    Optional<User> findById(int id);

    Optional<User> findByEmail (String email);

    Optional<User> findByUsername(String username);

    User save (User user);
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
    void deleteById(int id);
}
