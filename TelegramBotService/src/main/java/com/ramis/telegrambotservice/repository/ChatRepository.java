package com.ramis.telegrambotservice.repository;

import com.ramis.telegrambotservice.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {


}
