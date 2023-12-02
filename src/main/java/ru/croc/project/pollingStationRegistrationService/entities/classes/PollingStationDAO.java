package ru.croc.project.pollingStationRegistrationService.entities.classes;

import ru.croc.project.pollingStationRegistrationService.entities.PollingStation;

import java.util.List;

public interface PollingStationDAO {
    void registerPollingStation(PollingStation pollingStation);
    void deletePollingStation(long userId, long pollingStationId);
    List<PollingStation> getAllPollingStations(long chatId);
    PollingStation getPollingStationById(long id);

}
