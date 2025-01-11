package io.techchamps.restbackend.controller;

import io.techchamps.restbackend.entity.Adresses;
import io.techchamps.restbackend.entity.Role;
import io.techchamps.restbackend.entity.RoleName;
import io.techchamps.restbackend.entity.User;
import io.techchamps.restbackend.exception.NotFoundException;
import io.techchamps.restbackend.exception.UnauthorizedException;
import io.techchamps.restbackend.repository.AdressesRepository;
import io.techchamps.restbackend.repository.RoleRepository;
import io.techchamps.restbackend.request.AdressRequest;
import io.techchamps.restbackend.request.UserRequest;
import io.techchamps.restbackend.response.ErrorResponse;
import io.techchamps.restbackend.response.UserResponse;
import io.techchamps.restbackend.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserApi {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AdressesRepository adressesRepository;

    // Method to get all users

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            List<UserResponse> userResponses = users.stream()
                    .map(user -> modelMapper.map(user, UserResponse.class))
                    .toList();
            return ResponseEntity.ok(userResponses);
            // Success response
        } catch (UnauthorizedException e) {
            ErrorResponse error = new ErrorResponse("Access Denied", HttpStatus.UNAUTHORIZED.value(), "access_denied");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        catch (Exception e) {
            // Handle other errors
            ErrorResponse error = new ErrorResponse("An error occurred while retrieving users", HttpStatus.INTERNAL_SERVER_ERROR.value(), "server_error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);  // Error response
        }
    }

    // Method to get user by ID
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable int id) {
        try {
            User user = userService.findById(id).orElseThrow(() -> new NotFoundException("User with ID " + id + " not found!"));
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
            return ResponseEntity.ok(userResponse);
        } catch (NotFoundException e) {
            ErrorResponse error = new ErrorResponse("User not found", HttpStatus.NOT_FOUND.value(), "user_not_found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (UnauthorizedException e) {
            ErrorResponse error = new ErrorResponse("Access Denied", HttpStatus.FORBIDDEN.value(), "access_denied");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value(), "server_error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Method to add a new user
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody UserRequest userRequest) {
        try {
            // Assign roles to the user
            Set<Role> roles = Set.of(roleRepository.findByName(RoleName.USER).orElseThrow(() -> new NotFoundException("Role not found")));

            // Map UserRequest to User entity
            User user = modelMapper.map(userRequest, User.class);
            user.setRoles(roles);

            // Encrypt password (using PasswordEncoder)
            user.setPassword(encoder.encode(userRequest.getPassword()));

            for (AdressRequest adressRequest : userRequest.getAdresses()) {
                Adresses adresses = modelMapper.map(adressRequest, Adresses.class);
                adressesRepository.save(adresses);
            }
            // Save user
            userService.save(user);

            // Map saved user to UserResponse
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);

            return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
        } catch (NotFoundException e) {
            ErrorResponse error = new ErrorResponse("Role not found", HttpStatus.NOT_FOUND.value(), "role_not_found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("An error occurred while creating the user", HttpStatus.INTERNAL_SERVER_ERROR.value(), "server_error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Method to delete a user by ID
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable int id) {
        try {
            User user = userService.findById(id).orElseThrow(() -> new NotFoundException("User with ID " + id + " not found!"));
            userService.deleteById(id);
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
            return ResponseEntity.ok(userResponse);
        } catch (NotFoundException e) {
            ErrorResponse error = new ErrorResponse("User not found", HttpStatus.NOT_FOUND.value(), "user_not_found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("An error occurred while deleting the user", HttpStatus.INTERNAL_SERVER_ERROR.value(), "server_error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
