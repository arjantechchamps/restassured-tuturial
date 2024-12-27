package io.techchamps.restbackend.services;

import io.techchamps.restbackend.entity.User;
import io.techchamps.restbackend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    private User user;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        // Set up a mock user to use in the tests
        user = new User("UnitTest", "UnitTest", "unittest@example.com", "Unit1234!");
        user.setId(1);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void loadUserByUsername_ShouldReturnUserPrinciple_WhenUserExists() {
        String username = "UnitTest";
        when(userRepository.findByUsername(username)).thenReturn(java.util.Optional.of(user));

        UserPrinciple userPrinciple = (UserPrinciple) userDetailsServiceImpl.loadUserByUsername(username);

        assertNotNull(userPrinciple);
        assertEquals(user.getUsername(), userPrinciple.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFoundException_WhenUserDoesNotExist() {
        String username = "nonexistentUser";
        when(userRepository.findByUsername(username)).thenReturn(java.util.Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsServiceImpl.loadUserByUsername(username);
        });

        assertEquals("User Not Found with -> username or email : " + username, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }
}
