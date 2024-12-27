package io.techchamps.restbackend.request;

public class ReplyMessageDto {

    private String content;
    private String username;

    public ReplyMessageDto(String content, String username) {
        this.content = content;
        this.username = username;
    }

    public ReplyMessageDto() {

    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }
}