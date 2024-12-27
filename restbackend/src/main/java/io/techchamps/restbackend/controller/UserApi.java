package io.techchamps.restbackend.controller;

import io.techchamps.restbackend.config.Constants;
import io.techchamps.restbackend.entity.Role;
import io.techchamps.restbackend.entity.RoleName;
import io.techchamps.restbackend.entity.User;
import io.techchamps.restbackend.exception.NotFoundException;

import io.techchamps.restbackend.repository.RoleRepository;
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
@RequestMapping(value ="/api", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserApi {


    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    UserService userService;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    private static final String ROLE_NOT_FOUND ="ERROR: Role not found";

    @GetMapping(value = "/users")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {

        List<User> listofUser = userService.getAllUsers();
        List<UserResponse> userResponseList = new ArrayList<>();
        for (User user : listofUser) {
            userResponseList.add(modelMapper.map(user, UserResponse.class));
        }

        return userResponseList;
    }


    @GetMapping(value = "/users/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getuserById(@PathVariable("id") @Min(1) int id) {
        if (userService.findById(id).isPresent()) {
            Optional<User> user = userService.findById(id);
            UserResponse userResponse = modelMapper.map(user,UserResponse.class);
            return ResponseEntity.ok().body(userResponse);
        } else {
            throw new NotFoundException("User with " + id + " not found!");
        }
    }

    @GetMapping(value = "/users/email")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse>  getuserByEmail(@RequestParam(value = "email") String email) {

        if (userService.findByEmail(email).isPresent()) {
            Optional<User> user = userService.findByEmail(email);
            UserResponse userResponse = modelMapper.map(user,UserResponse.class);
            return ResponseEntity.ok().body(userResponse);
        } else {
           throw new NotFoundException("User with " + email + " not found!");
        }

    }


    @PostMapping(value = "/users")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> addUser(@Valid @RequestBody UserRequest userRequest) {

        //Convert roles from string to Role Enum first
        Set<Role> roles = new HashSet<>();
        Set<String> strRoles = userRequest.getRoles();
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new NotFoundException(ROLE_NOT_FOUND));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin","ADMIN" -> {
                        Role adminrole = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow(() -> new NotFoundException(ROLE_NOT_FOUND));
                        roles.add(adminrole);
                    }
                    case "pm","MODERATOR","moderator" -> {
                        Role modRole = roleRepository.findByName(RoleName.ROLE_MODERATOR)
                                .orElseThrow(() -> new NotFoundException("Error: Role is not found."));
                        roles.add(modRole);

                    }
                    case "user","USER" -> {
                        Role adminrole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new NotFoundException(ROLE_NOT_FOUND));
                        roles.add(adminrole);
                    }
                    default -> {
                        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                                .orElseThrow(() -> new NotFoundException("Error: Role is not found."));
                        roles.add(userRole);
                    }


                }
            });
        }
            User user = modelMapper.map(userRequest, User.class);
            user.setRoles(roles);
            user.setPassword(encoder.encode(Constants.DEFAULT_PASSWORD));
            userService.save(user);
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
            // get id of the saved user and put in into responseobject
            Optional<User> user1 = userService.findByUsername((user.getUsername()));
            user1.ifPresent(value -> userResponse.setId(value.getId()));
            return ResponseEntity.ok().body(userResponse);
        }


        @DeleteMapping("/users/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<UserResponse>  deleteEmployee ( @PathVariable int id){

            if (userService.findById(id).isPresent()) {
                Optional<User> user = userService.findById(id);
                UserResponse userResponse = modelMapper.map(user,UserResponse.class);
                userService.deleteById(id);
                return ResponseEntity.ok().body(userResponse);
            } else {
                throw new NotFoundException("id niet gevonden");
            }
        }

    @GetMapping("/profile/{id}")
    public ResponseEntity<String> getUserProfile(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("This endpoint is not implemented yet.");
    }
}

