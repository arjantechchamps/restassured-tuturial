package io.techchamps.restbackend.api;

import io.techchamps.restbackend.jwt.JwtProvider;
import io.techchamps.restbackend.repository.UserRepository;
import io.techchamps.restbackend.repository.RoleRepository;
import io.techchamps.restbackend.request.LoginRequest;
import io.techchamps.restbackend.request.SignUpRequest;
import io.techchamps.restbackend.response.JwtResponse;
import io.techchamps.restbackend.services.UserPrinciple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthApiTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private io.techchamps.restbackend.controller.AuthAPI authAPI;

    @Mock
    private Authentication authentication;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        SecurityContext securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(createMockUserPrinciple());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testSignIn_ShouldReturnJwtToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("UnitTest");
        loginRequest.setPassword("UnitTest1234!");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(jwtProvider.generateJwtToken(authentication)).thenReturn("mockJwtToken");

        ResponseEntity<JwtResponse> response = authAPI.authenticateUser(loginRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("mockJwtToken", Objects.requireNonNull(response.getBody()).getToken());
    }

    @Test
    void registerUser_ShouldReturnBadRequest_WhenUsernameExists() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("register");
        signUpRequest.setEmail("john@example.com");
        signUpRequest.setPassword("password123");
        signUpRequest.setRoles(new HashSet<>(Arrays.asList("USER")));

        when(userRepository.existsByUsername("register")).thenReturn(true);

        ResponseEntity<?> response = authAPI.registerUser(signUpRequest);

        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, String>> mapResponse = (ResponseEntity<Map<String, String>>) response;

        String errorMessage = Objects.requireNonNull(mapResponse.getBody()).get("error");

        assertNotNull(errorMessage);
        assertEquals("Username:register is already taken!", errorMessage);
    }



    @Test
    void testSignUp_ShouldReturnBadRequest_WhenEmailExists() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("register");
        signUpRequest.setEmail("john@example.com");
        signUpRequest.setPassword("password123");
        signUpRequest.setRoles(new HashSet<>(List.of("ROLE_USER")));

        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        ResponseEntity<?> response = authAPI.registerUser(signUpRequest);

        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, String>> mapResponse = (ResponseEntity<Map<String, String>>) response;
        String errorMessage = Objects.requireNonNull(mapResponse.getBody()).get("error");

        assertNotNull(errorMessage);
        assertEquals("Email john@example.com is already in use!", errorMessage);
    }

    private UserPrinciple createMockUserPrinciple() {
        return new UserPrinciple(
                10,
                "UnitTest",
                "UnitTest",
                "unitTest@example.com",
                "unit1234!",
                List.of(new SimpleGrantedAuthority("USER"),new SimpleGrantedAuthority("USER"))
        );
    }
}
