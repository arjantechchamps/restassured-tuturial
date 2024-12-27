package io.techchamps.restbackend.response;

import java.time.LocalDateTime;

public class MessageDto {
    private Long id;
    private String content;
    private String imageData;  // Base64 string for image
    private LocalDateTime timestamp;
    private String username;
    private int likes;
    private boolean liked;

    public MessageDto() {}

    public MessageDto(Long id, String content, String imageData, LocalDateTime timestamp, String username, int likes, boolean liked) {
        this.id = id;
        this.content = content;
        this.imageData = imageData;
        this.timestamp = timestamp;
        this.username = username;
        this.likes = likes;
        this.liked = liked;
    }

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

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
