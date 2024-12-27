package io.techchamps.restbackend.repository;

import io.techchamps.restbackend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message,Long> {
}
