package io.techchamps.restbackend.controller;

import io.techchamps.restbackend.entity.Like;
import io.techchamps.restbackend.entity.Message;
import io.techchamps.restbackend.entity.ReplyMessage;
import io.techchamps.restbackend.entity.User;
import io.techchamps.restbackend.exception.NotFoundException;
import io.techchamps.restbackend.repository.*;
import io.techchamps.restbackend.request.ReplyMessageDto;
import io.techchamps.restbackend.response.MessageDto;
import io.techchamps.restbackend.response.MessageWithResponsesDto;
import io.techchamps.restbackend.services.UserPrinciple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/messages", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageApi {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    ReplyMessageRepository replyMessageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LikeRepository likeRepository;

    private static final Logger logger = LoggerFactory.getLogger(MessageApi.class);

    private static final String USER_NOT_FOUND = "User not found";

    private static final String MESSAGE_NOT_FOUND= "Message not found";

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<MessageWithResponsesDto>> getAllMessages(Authentication authentication) {
        List<Message> messages = messageRepository.findAll();
        List<MessageWithResponsesDto> messageWithResponses = new ArrayList<>();

        User currentUser = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserPrinciple userPrinciple) {
            String currentUsername = userPrinciple.getUsername();
            currentUser = userRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        }

        for (Message message : messages) {
            boolean liked = currentUser != null && likeRepository.findByMessageAndUser(message, currentUser).isPresent();

            MessageDto messageDto = new MessageDto(
                    message.getId(),
                    message.getContent(),
                    message.getImageData(),
                    message.getTimestamp(),
                    message.getUser().getUsername(),
                    message.getLikes(),
                    liked
            );

            List<ReplyMessageDto> replyDtos = replyMessageRepository.findByMessage(message)
                    .stream()
                    .map(reply -> new ReplyMessageDto(reply.getContent(), reply.getUser().getUsername()))
                    .toList();

            messageWithResponses.add(new MessageWithResponsesDto(messageDto, replyDtos));
        }

        logger.info("Messages with Responses: {}", messageWithResponses);
        return ResponseEntity.ok(messageWithResponses);
    }
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Message> postMessage(
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserPrinciple) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));

        Message message = new Message();
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setUser(user);
        message.setLikes(0);

        if (image != null && !image.isEmpty()) {
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            message.setImageData(base64Image);
        }

        Message savedMessage = messageRepository.save(message);
        return ResponseEntity.status(201).body(savedMessage);
    }

    @PostMapping("/{messageId}/like")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Integer> likeMessage(@PathVariable Long messageId, Authentication authentication) {
        String username = ((UserPrinciple) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_NOT_FOUND));

        Optional<Like> existingLike = likeRepository.findByMessageAndUser(message, user);
        if (existingLike.isPresent()) {
            return ResponseEntity.badRequest().body(message.getLikes());
        }

        Like like = new Like();
        like.setMessage(message);
        like.setUser(user);
        likeRepository.save(like);

        message.setLikes(message.getLikes() + 1);
        messageRepository.save(message);

        return ResponseEntity.ok(message.getLikes());
    }

    @DeleteMapping("/{messageId}/like")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Integer> unlikeMessage(@PathVariable Long messageId, Authentication authentication) {
        String username = ((UserPrinciple) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_NOT_FOUND));

        Optional<Like> existingLike = likeRepository.findByMessageAndUser(message, user);
        if (existingLike.isEmpty()) {
            return ResponseEntity.badRequest().body(message.getLikes());
        }

        likeRepository.delete(existingLike.get());

        message.setLikes(message.getLikes() - 1);
        messageRepository.save(message);

        return ResponseEntity.ok(message.getLikes());
    }

    @DeleteMapping("/{messageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        messageRepository.deleteById(messageId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{messageId}/reply")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ReplyMessageDto postReply(
            @PathVariable Long messageId,
            @RequestBody ReplyMessage replyMessage) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserPrinciple userPrinciple) {
            String currentUsername = userPrinciple.getUsername();

            User currentUser = userRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));

            Message message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new NotFoundException(MESSAGE_NOT_FOUND));

            replyMessage.setMessage(message);
            replyMessage.setTimestamp(LocalDateTime.now());
            replyMessage.setUser(currentUser);

            ReplyMessage savedReply = replyMessageRepository.save(replyMessage);

            return new ReplyMessageDto(savedReply.getContent(), currentUser.getUsername());
        } else {
            throw new NotFoundException("User is not authenticated");
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Message with ID " + id + " not found!"));

        return ResponseEntity.ok(message);
    }
}
