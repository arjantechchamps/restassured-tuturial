package io.techchamps.restbackend.api;

import io.techchamps.restbackend.controller.UserApi;
import io.techchamps.restbackend.entity.Role;
import io.techchamps.restbackend.entity.RoleName;
import io.techchamps.restbackend.entity.User;
import io.techchamps.restbackend.exception.NotFoundException;
import io.techchamps.restbackend.repository.RoleRepository;
import io.techchamps.restbackend.request.UserRequest;
import io.techchamps.restbackend.response.UserResponse;
import io.techchamps.restbackend.services.UserPrinciple;
import io.techchamps.restbackend.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserApiTest {

    private AutoCloseable closeable;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserApi userApi;

    @Mock
    private UserService userService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder encoder;

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
    void testGetAllUsers() {
        // Mock data
        User user1 = new User();
        user1.setId(1);
        user1.setEmail("user1@test.com");

        User user2 = new User();
        user2.setId(2);
        user2.setEmail("user2@test.com");

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        UserResponse userResponse1 = new UserResponse();
        userResponse1.setId(1);
        userResponse1.setEmail("user1@test.com");

        UserResponse userResponse2 = new UserResponse();
        userResponse2.setId(2);
        userResponse2.setEmail("user2@test.com");

        when(modelMapper.map(user1, UserResponse.class)).thenReturn(userResponse1);
        when(modelMapper.map(user2, UserResponse.class)).thenReturn(userResponse2);

        // Test
        List<UserResponse> response = userApi.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("user1@test.com", response.get(0).getEmail());
        assertEquals("user2@test.com", response.get(1).getEmail());

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUserById() {
        // Mock data
        int userId = 1;
        User user = new User();
        user.setId(userId);
        user.setEmail("user1@test.com");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId);
        userResponse.setEmail("user1@test.com");

        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponse.class)).thenReturn(userResponse);

        // Test
        ResponseEntity<UserResponse> response = userApi.getUserById(userId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("user1@test.com", Objects.requireNonNull(response.getBody()).getEmail());

        verify(userService, times(1)).findById(userId);
    }

    @Test
    void testGetUserByIdNotFound() {
        // Mock data
        int userId = 99;

        when(userService.findById(userId)).thenReturn(Optional.empty());

        // Test & Assert
        Exception exception = assertThrows(NotFoundException.class, () -> userApi.getUserById(userId));
        assertEquals("User with ID 99 not found!", exception.getMessage());

        verify(userService, times(1)).findById(userId);
    }

    @Test
    void testAddUser() {
        // Mock data
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("newuser@test.com");
        userRequest.setRoles(Set.of("user"));

        User user = new User();
        user.setId(1);
        user.setEmail("newuser@test.com");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(1);
        userResponse.setEmail("newuser@test.com");

        // Mock roles
        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName(RoleName.ROLE_USER);

        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(modelMapper.map(userRequest, User.class)).thenReturn(user);
        when(encoder.encode(anyString())).thenReturn("encoded_password");
        when(userService.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponse.class)).thenReturn(userResponse);

        // Test
        ResponseEntity<UserResponse> response = userApi.addUser(userRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("newuser@test.com", response.getBody().getEmail());

        verify(userService, times(1)).save(user);
        verify(roleRepository, times(1)).findByName(RoleName.ROLE_USER);
    }

    @Test
    void testDeleteUser() {
        // Mock data
        int userId = 1;
        User user = new User();
        user.setId(userId);
        user.setEmail("user1@test.com");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId);
        userResponse.setEmail("user1@test.com");

        when(userService.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userService).deleteById(userId);
        when(modelMapper.map(user, UserResponse.class)).thenReturn(userResponse);

        // Test
        ResponseEntity<UserResponse> response = userApi.deleteUser(userId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("user1@test.com", Objects.requireNonNull(response.getBody()).getEmail());

        verify(userService, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUserNotFound() {
        // Mock data
        int userId = 99;

        when(userService.findById(userId)).thenReturn(Optional.empty());

        // Test & Assert
        Exception exception = assertThrows(NotFoundException.class, () -> userApi.deleteUser(userId));
        assertEquals("User with ID 99 not found!", exception.getMessage());

        verify(userService, times(1)).findById(userId);
        verify(userService, never()).deleteById(anyInt());
    }

    private UserPrinciple createMockUserPrinciple() {
        return new UserPrinciple(
                10,
                "UnitTest",
                "UnitTest",
                "unitTest@example.com",
                "unit1234!",
                List.of(new SimpleGrantedAuthority("ROLE_USER"),new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }
}
