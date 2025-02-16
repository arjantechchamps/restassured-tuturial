package io.techchamps.restbackend.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.techchamps.restbackend.entity.*;
import io.techchamps.restbackend.exception.NotFoundException;
import io.techchamps.restbackend.exception.UnauthorizedException;
import io.techchamps.restbackend.repository.RoleRepository;
import io.techchamps.restbackend.request.AddressRequest;
import io.techchamps.restbackend.request.ProfileRequest;
import io.techchamps.restbackend.request.UserRequest;
import io.techchamps.restbackend.response.ErrorResponse;
import io.techchamps.restbackend.response.JwtResponse;
import io.techchamps.restbackend.response.UserResponse;
import io.techchamps.restbackend.services.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.Set;
import java.util.stream.Collectors;

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

    @PreAuthorize("hasRole('ADMIN')")
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful authentication", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserRequest userRequest) {
        try {
            // Assign roles to the user
            Set<Role> roles = Set.of(roleRepository.findByName(RoleName.USER)
                    .orElseThrow(() -> new NotFoundException("Role USER not found")));

            User user = new User();
            user.setName(userRequest.getName());
            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getEmail());
            user.setPassword(encoder.encode(userRequest.getPassword())); // Encode password
            user.setRoles(roles);

            // Handle optional profile information
            if (userRequest.getProfile() != null) {
                ProfileRequest profileRequest = userRequest.getProfile();

                // Add addresses
                if (profileRequest.getAddresses() != null) {
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
                }

                // Add interests
                if (profileRequest.getInterests() != null) {
                    user.getInterests().addAll(profileRequest.getInterests());
                }
            }

            // Save user
            User savedUser = userService.save(user);

            // Map to UserResponse
            UserResponse userResponse = mapToUserResponse(savedUser);

            // Return a success response
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Role not found", 400, "role_not_found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error creating user", 500, "server_error"));
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/username/{username}")
    public ResponseEntity<Object> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new NotFoundException("User with username " + username + " not found!"));
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
            return ResponseEntity.ok(userResponse);
        } catch (NotFoundException e) {
            ErrorResponse error = new ErrorResponse("User not found", HttpStatus.NOT_FOUND.value(), "user_not_found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value(), "server_error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name()) // Convert RoleName enum to String
                .collect(Collectors.toSet()));

        // Map profile details if available
        if (!user.getAddresses().isEmpty() || !user.getInterests().isEmpty()) {
            ProfileRequest profileRequest = new ProfileRequest();

            // Map addresses
            profileRequest.setAddresses(user.getAddresses().stream().map(address -> {
                AddressRequest addressRequest = new AddressRequest();
                addressRequest.setStreet(address.getStreet());
                addressRequest.setHouseNumber(address.getHouseNumber());
                addressRequest.setZipcode(address.getZipcode());
                addressRequest.setCity(address.getCity());
                addressRequest.setCountry(address.getCountry());
                return addressRequest;
            }).toList());

            // Map interests
            profileRequest.setInterests(user.getInterests());

            userResponse.setProfile(profileRequest);
        }

        return userResponse;
    }


    // Method to delete a user by ID
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable int id) {
        try {
            User user = userService.findById(id).orElseThrow(() -> new NotFoundException("User with ID " + id + " not found!"));
            userService.deleteById(id);
            UserResponse userResponse = mapToUserResponse(user);
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
