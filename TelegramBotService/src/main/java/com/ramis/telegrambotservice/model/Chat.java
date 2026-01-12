package com.ramis.telegrambotservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "chats")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Chat {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

}
