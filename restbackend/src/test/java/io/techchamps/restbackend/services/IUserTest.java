package io.techchamps.restbackend.services;

import io.techchamps.restbackend.entity.Role;
import io.techchamps.restbackend.entity.RoleName;
import io.techchamps.restbackend.entity.User;
import io.techchamps.restbackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IUserTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        User user = mockedUser();
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("UnitTest", result.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        User user = mockedUser();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1);

        assertTrue(result.isPresent());
        assertEquals("UnitTest", result.get().getName());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(1);

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void save_ShouldPersistUser() {
        User user = mockedUser();
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.save(user);

        assertEquals("UnitTest", result.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
        when(userRepository.existsByUsername("john123")).thenReturn(true);

        Boolean result = userService.existsByUsername("john123");

        assertTrue(result);
        verify(userRepository, times(1)).existsByUsername("john123");
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        Boolean result = userService.existsByEmail("john@example.com");

        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail("john@example.com");
    }

    @Test
    void deleteById_ShouldCallDeleteOnRepository() {
        doNothing().when(userRepository).deleteById(1);

        userService.deleteById(1);

        verify(userRepository, times(1)).deleteById(1);
    }

    private static User mockedUser() {
        Set<Role> roles = Set.of(new Role(RoleName.USER));
        User user = new User("UnitTest", "UnitTest", "unitTest@example.com", "Unit1234!");
        user.setId(1);
        user.setRoles(roles);
        return user;
    }
}
