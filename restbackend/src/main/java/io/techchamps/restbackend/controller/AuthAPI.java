package io.techchamps.restbackend.controller;

import io.techchamps.restbackend.entity.Role;
import io.techchamps.restbackend.entity.RoleName;
import io.techchamps.restbackend.entity.User;
import io.techchamps.restbackend.jwt.JwtProvider;
import io.techchamps.restbackend.repository.RoleRepository;
import io.techchamps.restbackend.repository.UserRepository;
import io.techchamps.restbackend.request.LoginRequest;
import io.techchamps.restbackend.request.SignUpRequest;
import io.techchamps.restbackend.response.JwtResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.lang.constant.Constable;

import java.util.*;
@SuppressWarnings("java:S1452")
@RestController
@RequestMapping(value = "/api/auth", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthAPI {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;

    private static final String ERROR_MESSAGE = "error";

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt,authentication));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
            return ResponseEntity.badRequest()
                    .body(Map.of(ERROR_MESSAGE,  "Username:"+signUpRequest.getUsername()+ " is already taken!"));
        }

        if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
            return ResponseEntity.badRequest()
                    .body(Map.of(ERROR_MESSAGE,  "Email "+signUpRequest.getEmail()+" is already in use!"));
        }

        User user = modelMapper.map(signUpRequest, User.class);
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<String> requestRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (requestRoles != null && !requestRoles.isEmpty()) {
            for (String roleName : requestRoles) {
                try {
                    Role role = roleRepository.findByName(RoleName.valueOf(roleName))
                            .orElseThrow(() -> new RuntimeException("ERROR: Role not found"));
                    roles.add(role);
                } catch (RuntimeException e) {
                    List<String> availableRoles = Arrays.stream(RoleName.values())
                            .map(Enum::name)
                            .toList();
                    return ResponseEntity.badRequest()
                            .body(Map.of(
                                    ERROR_MESSAGE, "Invalid role provided: " + roleName,
                                    "availableRoles", availableRoles
                            ));
                }
            }
        } else {
            // Assign default role
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("ERROR: Default role not found"));
            roles.add(userRole);
        }

        user.setRoles(roles);

        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "password", signUpRequest.getPassword()
        ));
    }



    @GetMapping("/logout")
    public Constable getLogoutPage(HttpServletRequest request, HttpServletResponse response){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        return "redirect:/signin";
    }

}
