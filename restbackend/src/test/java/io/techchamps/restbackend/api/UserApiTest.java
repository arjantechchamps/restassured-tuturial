package io.techchamps.restbackend.api;

import io.techchamps.restbackend.controller.UserApi;
import io.techchamps.restbackend.entity.Role;
import io.techchamps.restbackend.entity.RoleName;
import io.techchamps.restbackend.entity.User;
import io.techchamps.restbackend.exception.NotFoundException;
import io.techchamps.restbackend.repository.RoleRepository;
import io.techchamps.restbackend.request.UserRequest;
import io.techchamps.restbackend.response.UserResponse;
import io.techchamps.restbackend.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserApiTest {

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

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        List<User> users = List.of(new User());
        when(userService.getAllUsers()).thenReturn(users);
        when(modelMapper.map(any(User.class), eq(UserResponse.class)))
                .thenReturn(new UserResponse());

        List<UserResponse> result = userApi.getAllUsers();

        assertEquals(1, result.size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        User user = new User();
        user.setId(1);
        UserResponse userResponse = new UserResponse();
        when(userService.findById(1)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponse.class)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userApi.getuserById(1);

        assertEquals(200, response.getStatusCode().value());
        verify(userService, times(2)).findById(1); // One for check, one for mapping
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserDoesNotExist() {
        when(userService.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userApi.getuserById(1));
        verify(userService, times(1)).findById(1);
    }

    @Test
    void addUser_ShouldSaveUser_WhenValidRequest() {
        UserRequest userRequest = new UserRequest();
        userRequest.setRoles(Set.of("USER"));
        Role userRole = new Role(RoleName.ROLE_USER);
        User user = new User();
        UserResponse userResponse = new UserResponse();
        String mockEncodedPassword = "encoded_password";

        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(modelMapper.map(userRequest, User.class)).thenReturn(user);
        when(modelMapper.map(user, UserResponse.class)).thenReturn(userResponse);
        when(encoder.encode(any(CharSequence.class))).thenReturn(mockEncodedPassword);
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        ResponseEntity<UserResponse> response = userApi.addUser(userRequest);

        assertEquals(200, response.getStatusCode().value());
        verify(encoder, times(1)).encode(any(CharSequence.class));
        verify(userService, times(1)).save(user);
    }
    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        User user = new User();
        user.setId(1);
        UserResponse userResponse = new UserResponse();
        when(userService.findById(1)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponse.class)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userApi.deleteEmployee(1);

        assertEquals(200, response.getStatusCode().value());
        verify(userService, times(1)).deleteById(1);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserDoesNotExist() {
        when(userService.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userApi.deleteEmployee(1));
        verify(userService, times(1)).findById(1);
    }
}
