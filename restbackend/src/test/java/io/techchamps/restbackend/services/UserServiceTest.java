package io.techchamps.restbackend.services;

import io.techchamps.restbackend.entity.User;
import io.techchamps.restbackend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        user = new User("UnitTest", "UnitTest", "unitTest@example.com", "Unit1234!");
        user.setId(1); // Set user ID
    }
    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers_WhenUsersExist() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user.getUsername(), users.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findById(1);

        assertTrue(foundUser.isPresent());
        assertEquals(user.getUsername(), foundUser.get().getUsername());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.findById(99);

        assertFalse(foundUser.isPresent());
        verify(userRepository, times(1)).findById(99);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        String email = "john@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findByEmail(email);

        assertTrue(foundUser.isPresent());
        assertEquals(user.getEmail(), foundUser.get().getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUsernameExists() {
        String username = "john123";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findByUsername(username);

        assertTrue(foundUser.isPresent());
        assertEquals(user.getUsername(), foundUser.get().getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void save_ShouldReturnSavedUser() {
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.save(user);

        assertNotNull(savedUser);
        assertEquals(user.getUsername(), savedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
        String username = "UnitTest";
        when(userRepository.existsByUsername(username)).thenReturn(true);

        Boolean exists = userService.existsByUsername(username);

        assertTrue(exists);
        verify(userRepository, times(1)).existsByUsername(username);
    }

    @Test
    void existsByUsername_ShouldReturnFalse_WhenUsernameDoesNotExist() {
        String username = "nonexistentUsername";
        when(userRepository.existsByUsername(username)).thenReturn(false);

        Boolean exists = userService.existsByUsername(username);

        assertFalse(exists);
        verify(userRepository, times(1)).existsByUsername(username);
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        String email = "john@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        Boolean exists = userService.existsByEmail(email);

        assertTrue(exists);
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        String email = "nonexistent@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        Boolean exists = userService.existsByEmail(email);

        assertFalse(exists);
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void deleteById_ShouldDeleteUser_WhenUserExists() {
        int userId = 1;
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}