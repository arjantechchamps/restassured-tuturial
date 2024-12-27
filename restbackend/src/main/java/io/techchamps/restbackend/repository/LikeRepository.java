package io.techchamps.restbackend.repository;

import io.techchamps.restbackend.entity.Like;
import io.techchamps.restbackend.entity.Message;
import io.techchamps.restbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByMessageAndUser(Message message, User user);
    long countByMessage(Message message);
}

