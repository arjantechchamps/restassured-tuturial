package io.techchamps.restbackend.api;

import io.techchamps.restbackend.controller.MessageApi;
import io.techchamps.restbackend.entity.Like;
import io.techchamps.restbackend.entity.Message;
import io.techchamps.restbackend.entity.ReplyMessage;
import io.techchamps.restbackend.entity.User;
import io.techchamps.restbackend.repository.LikeRepository;
import io.techchamps.restbackend.repository.MessageRepository;
import io.techchamps.restbackend.repository.ReplyMessageRepository;
import io.techchamps.restbackend.repository.UserRepository;
import io.techchamps.restbackend.response.MessageWithResponsesDto;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageApiTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ReplyMessageRepository replyMessageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private MessageApi messageApi;

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
    void getAllMessages_ShouldReturnMessagesWithResponses() {
        User user = new User();
        user.setId(10);
        user.setUsername("UnitTest");

        Message message = new Message();
        message.setId(5L);
        message.setContent("Test message");
        message.setUser(user);
        message.setLikes(10);

        ReplyMessage reply = new ReplyMessage();
        reply.setId(5L);
        reply.setContent("Test reply");
        reply.setUser(user);
        reply.setMessage(message);

        when(authentication.getPrincipal()).thenReturn(createMockUserPrinciple());
        when(userRepository.findByUsername("UnitTest")).thenReturn(Optional.of(user));
        when(messageRepository.findAll()).thenReturn(List.of(message));
        when(replyMessageRepository.findByMessage(message)).thenReturn(List.of(reply));
        when(likeRepository.findByMessageAndUser(message, user)).thenReturn(Optional.empty());

        ResponseEntity<List<MessageWithResponsesDto>> response = messageApi.getAllMessages(authentication);

        assertNotNull(response);
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        MessageWithResponsesDto dto = response.getBody().get(0);
        assertEquals("Test message", dto.getMessage().getContent());
        assertEquals(1, dto.getResponses().size());
        assertEquals("Test reply", dto.getResponses().get(0).getContent());
    }

    @Test
    void postMessage_ShouldSaveMessage_WhenValidRequest() throws Exception {
        User user = new User();
        user.setId(10);
        user.setUsername("UnitTest");

        Message message = new Message();
        message.setId(1L);
        message.setContent("Test content");
        message.setUser(user);

        when(authentication.getPrincipal()).thenReturn(createMockUserPrinciple());
        when(userRepository.findByUsername("UnitTest")).thenReturn(Optional.of(user));
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        ResponseEntity<Message> response = messageApi.postMessage("Test content", null);

        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertEquals("Test content", Objects.requireNonNull(response.getBody()).getContent());
        assertEquals("UnitTest", response.getBody().getUser().getUsername());
    }

    @Test
    void likeMessage_ShouldIncrementLikes_WhenUserHasNotLikedBefore() {
        User user = new User();
        user.setId(10);
        user.setUsername("UnitTest");

        Message message = new Message();
        message.setId(1L);
        message.setLikes(10);

        when(authentication.getPrincipal()).thenReturn(createMockUserPrinciple());
        when(userRepository.findByUsername("UnitTest")).thenReturn(Optional.of(user));
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(likeRepository.findByMessageAndUser(message, user)).thenReturn(Optional.empty());

        ResponseEntity<Integer> response = messageApi.likeMessage(1L, authentication);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(11, response.getBody());
        verify(messageRepository).save(message);
    }

    @Test
    void unlikeMessage_ShouldDecrementLikes_WhenUserHasLikedBefore() {
        User user = new User();
        user.setId(10);
        user.setUsername("UnitTest");

        Message message = new Message();
        message.setId(1L);
        message.setLikes(10);

        Like like = new Like();
        like.setId(1L);
        like.setMessage(message);
        like.setUser(user);

        when(authentication.getPrincipal()).thenReturn(createMockUserPrinciple());
        when(userRepository.findByUsername("UnitTest")).thenReturn(Optional.of(user));
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(likeRepository.findByMessageAndUser(message, user)).thenReturn(Optional.of(like));

        ResponseEntity<Integer> response = messageApi.unlikeMessage(1L, authentication);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(9, response.getBody());
        verify(likeRepository).delete(like);
        verify(messageRepository).save(message);
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
