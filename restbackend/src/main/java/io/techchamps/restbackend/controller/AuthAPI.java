package io.techchamps.restbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.techchamps.restbackend.entity.Role;
import io.techchamps.restbackend.entity.RoleName;
import io.techchamps.restbackend.entity.User;
import io.techchamps.restbackend.jwt.JwtProvider;
import io.techchamps.restbackend.repository.RoleRepository;
import io.techchamps.restbackend.repository.UserRepository;
import io.techchamps.restbackend.request.LoginRequest;
import io.techchamps.restbackend.request.SignUpRequest;
import io.techchamps.restbackend.response.ErrorResponse;
import io.techchamps.restbackend.response.JwtResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    @Operation(summary = "Authenticate user", description = "Authenticates the user and returns a JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful authentication", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtProvider.generateJwtToken(authentication);
            return ResponseEntity.ok(new JwtResponse(jwt, authentication));

        } catch (BadCredentialsException ex) {
            ErrorResponse error = new ErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED.value(), "authentication_error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (AuthenticationException ex) {
            ErrorResponse error = new ErrorResponse("Authentication failed", HttpStatus.UNAUTHORIZED.value(), "authentication_error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception ex) {
            // Catching any other unexpected exceptions
            ErrorResponse error = new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value(), "server_error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
            ErrorResponse error = new ErrorResponse("Username is already taken", HttpStatus.NOT_FOUND.value(), ERROR_MESSAGE);
            return ResponseEntity.badRequest().body(error);
        }

        if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
            ErrorResponse error = new ErrorResponse("Email is already in use", HttpStatus.NOT_FOUND.value(), ERROR_MESSAGE);
            return ResponseEntity.badRequest().body(error);
        }

        User user = modelMapper.map(signUpRequest, User.class);
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<String> requestRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (requestRoles != null && !requestRoles.isEmpty()) {
            for (String roleName : requestRoles) {
                try {
                    Role role = roleRepository.findByName(RoleName.valueOf(roleName))
                            .orElseThrow(() -> new RuntimeException("Role not found"));
                    roles.add(role);
                } catch (RuntimeException e) {
                    List<String> availableRoles = Arrays.stream(RoleName.values())
                            .map(Enum::name)
                            .toList();
                    ErrorResponse error = new ErrorResponse("Invalid role provided", HttpStatus.NOT_FOUND.value(), ERROR_MESSAGE);
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", error,
                            "availableRoles", availableRoles
                    ));
                }
            }
        } else {
            Role userRole = roleRepository.findByName(RoleName.USER)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
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
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        return ResponseEntity.ok("Logout successful");
    }
}
