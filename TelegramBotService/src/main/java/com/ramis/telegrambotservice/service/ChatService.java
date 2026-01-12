package com.ramis.telegrambotservice.service;

import com.ramis.telegrambotservice.model.Chat;
import com.ramis.telegrambotservice.repository.ChatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    @Transactional
    public void saveOrUpdate(Update update) {
        Long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getChat().getUserName();

        Chat chat = chatRepository.findById(chatId)
                .orElseGet(() -> {
                    Chat newChat = Chat.builder().id(chatId).username(username).build();
                    return chatRepository.save(newChat);
                });

        if (!Objects.equals(chat.getUsername(), username)) {
            chat.setUsername(username);
        }
    }

    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    public Chat getChat(Long id) {
        return chatRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Chat with id " + id + " not found")
        );
    }

}
