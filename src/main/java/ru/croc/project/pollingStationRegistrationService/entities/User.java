package ru.croc.project.pollingStationRegistrationService.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;


@Entity(name = "users_data_table")
@NoArgsConstructor
public class User {
    @Id
    private long chatId;
    private String name;

    public User(long chatId, String name) {
        this.chatId = chatId;
        this.name = name;
    }

    public long getChatId() {
        return chatId;
    }

    public String getName() {
        return name;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
