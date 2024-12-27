package io.techchamps.restbackend.response;

import io.techchamps.restbackend.request.ReplyMessageDto;

import java.util.List;

public class MessageWithResponsesDto {
    private MessageDto message;
    private List<ReplyMessageDto> responses;


    public MessageWithResponsesDto(){

    }
    public MessageWithResponsesDto(MessageDto message, List<ReplyMessageDto> responses) {
        this.message = message;
        this.responses = responses;
    }

    public MessageDto getMessage() {
        return message;
    }

    public void setMessage(MessageDto message) {
        this.message = message;
    }

    public List<ReplyMessageDto> getResponses() {
        return responses;
    }

    public void setResponses(List<ReplyMessageDto> responses) {
        this.responses = responses;
    }
}
