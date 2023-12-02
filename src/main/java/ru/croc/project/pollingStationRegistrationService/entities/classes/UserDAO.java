package ru.croc.project.pollingStationRegistrationService.entities.classes;

import ru.croc.project.pollingStationRegistrationService.entities.User;

public interface UserDAO {
    void registerUser(User user);
    User findById(long chatId);
}
