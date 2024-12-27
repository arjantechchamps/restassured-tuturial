package io.techchamps.restbackend.repository;

import io.techchamps.restbackend.entity.Message;
import io.techchamps.restbackend.entity.ReplyMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyMessageRepository extends JpaRepository<ReplyMessage, Long> {
    List<ReplyMessage> findByMessage(Message message);
}
