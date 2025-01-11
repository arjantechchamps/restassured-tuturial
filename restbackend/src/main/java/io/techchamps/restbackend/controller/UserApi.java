package io.techchamps.restbackend.controller;

import io.techchamps.restbackend.entity.*;
import io.techchamps.restbackend.exception.NotFoundException;
import io.techchamps.restbackend.exception.UnauthorizedException;
import io.techchamps.restbackend.repository.RoleRepository;
import io.techchamps.restbackend.request.ProfileRequest;
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

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody UserRequest userRequest) {
        try {

            // Assign roles to the user
            Set<Role> roles = Set.of(roleRepository.findByName(RoleName.USER).orElseThrow(() -> new NotFoundException("Role not found")));
            User user = new User();
            user.setName(userRequest.getName());
            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getEmail());
            user.setPassword(userRequest.getPassword()); // Ensure to encode the password
            user.setRoles(roles);

            // Handle optional profile information
            if (userRequest.getProfile() != null) {
                ProfileRequest profileRequest = userRequest.getProfile();

                // Add addresses
                List<Address> addresses = profileRequest.getAddresses().stream().map(req -> {
                    Address address = new Address();
                    address.setStreet(req.getStreet());
                    address.setHouseNumber(req.getHouseNumber());
                    address.setZipcode(req.getZipcode());
                    address.setCity(req.getCity());
                    address.setCountry(req.getCountry());
                    address.setUser(user);
                    return address;
                }).toList();
                user.getAddresses().addAll(addresses);

                // Add phone numbers
                List<PhoneNumber> phoneNumbers = profileRequest.getPhoneNumbers().stream().map(req -> {
                    PhoneNumber phoneNumber = new PhoneNumber();
                    phoneNumber.setNumber(req.getNumber());
                    phoneNumber.setType(req.getType());
                    phoneNumber.setUser(user);
                    return phoneNumber;
                }).toList();
                user.getPhoneNumbers().addAll(phoneNumbers);

                // Add interests
                if (profileRequest.getInterests() != null) {
                    user.getInterests().addAll(profileRequest.getInterests());
                }
            }

            userService.save(user);

            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error creating user", 500, "server_error"));
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
