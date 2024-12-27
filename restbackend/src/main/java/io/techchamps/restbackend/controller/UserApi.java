package io.techchamps.restbackend.controller;

import io.techchamps.restbackend.config.Constants;
import io.techchamps.restbackend.entity.Address;
import io.techchamps.restbackend.entity.Role;
import io.techchamps.restbackend.entity.RoleName;
import io.techchamps.restbackend.entity.User;
import io.techchamps.restbackend.exception.NotFoundException;
import io.techchamps.restbackend.repository.RoleRepository;
import io.techchamps.restbackend.request.AddressRequest;
import io.techchamps.restbackend.request.UserRequest;
import io.techchamps.restbackend.response.UserResponse;
import io.techchamps.restbackend.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api")
public class UserApi {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    UserService userService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    private static final String ROLE_NOT_FOUND = "ERROR: Role not found";

    @GetMapping(value = "/users")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        List<User> listOfUsers = userService.getAllUsers();
        List<UserResponse> userResponseList = new ArrayList<>();
        for (User user : listOfUsers) {
            userResponseList.add(mapUserToResponse(user));
        }
        return userResponseList;
    }

    @GetMapping(value = "/users/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") @Min(1) int id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok().body(mapUserToResponse(user.get()));
        } else {
            throw new NotFoundException("User with ID " + id + " not found!");
        }
    }

    @GetMapping(value = "/users/email")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam(value = "email") String email) {
        Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok().body(mapUserToResponse(user.get()));
        } else {
            throw new NotFoundException("User with email " + email + " not found!");
        }
    }

    @PostMapping(value = "/users")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> addUser(@Valid @RequestBody UserRequest userRequest) {
        User user = modelMapper.map(userRequest, User.class);
        user.setPassword(encoder.encode(Constants.DEFAULT_PASSWORD));

        // Convert roles from string to Role enum
        Set<Role> roles = new HashSet<>();
        Set<String> strRoles = userRequest.getRoles();
        if (strRoles == null) {
            roles.add(getRole(RoleName.ROLE_USER));
        } else {
            for (String role : strRoles) {
                switch (role.toLowerCase()) {
                    case "admin" -> roles.add(getRole(RoleName.ROLE_ADMIN));
                    case "moderator", "pm" -> roles.add(getRole(RoleName.ROLE_MODERATOR));
                    default -> roles.add(getRole(RoleName.ROLE_USER));
                }
            }
        }
        user.setRoles(roles);

        // Map and set addresses
        user.setWorkAddress(mapAddressRequestToEntity(userRequest.getWorkAddress()));
        user.setHomeAddress(mapAddressRequestToEntity(userRequest.getHomeAddress()));

        userService.save(user);

        return ResponseEntity.ok().body(mapUserToResponse(user));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable int id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.ok().body(mapUserToResponse(user.get()));
        } else {
            throw new NotFoundException("User with ID " + id + " not found!");
        }
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<String> getUserProfile(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("This endpoint is not implemented yet.");
    }

    // Helper methods
    private Role getRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException(ROLE_NOT_FOUND));
    }

    private Address mapAddressRequestToEntity(AddressRequest request) {
        if (request == null) {
            return null;
        }
        Address address = new Address();
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipcode(request.getZipcode());
        address.setCountry(request.getCountry());
        return address;
    }

    private AddressRequest mapAddressEntityToResponse(Address address) {
        if (address == null) {
            return null;
        }
        AddressRequest response = new AddressRequest();
        response.setStreet(address.getStreet());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setZipcode(address.getZipcode());
        response.setCountry(address.getCountry());
        return response;
    }

    private UserResponse mapUserToResponse(User user) {
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
        userResponse.setWorkAddress(mapAddressEntityToResponse(user.getWorkAddress()));
        userResponse.setHomeAddress(mapAddressEntityToResponse(user.getHomeAddress()));
        return userResponse;
    }
}
