package io.techchamps.restbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class Message {

    public Message(){
        // hide default constructor
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;

    @Lob
    private String imageData;  // Store Base64 string representation of image

    private LocalDateTime timestamp;
    private int likes;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReplyMessage> responses;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ReplyMessage> getResponses() {
        return responses;
    }

    public void setResponses(List<ReplyMessage> responses) {
        this.responses = responses;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        if (imageData != null) {
            this.imageData = Base64.getEncoder().encodeToString(imageData);
        } else {
            this.imageData = null;
        }
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }
}
