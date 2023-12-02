package ru.croc.project.pollingStationRegistrationService.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity(name="polling_station_table")
@NoArgsConstructor
public class PollingStation {
    @Id
    private long stationId;
    private long clientId;
    private String address;
    private int capacity;
    private int urnCount;
    private boolean hasSpecialEquipment;
    private String phoneNumber;

    private String workingHours;


    public PollingStation(long stationId, long clientId, String address, int capacity,
                          int urnCount, boolean hasSpecialEquipment, String phoneNumber, String workingHours) {
        this.stationId = stationId;
        this.clientId = clientId;
        this.address = address;
        this.capacity = capacity;
        this.urnCount = urnCount;
        this.hasSpecialEquipment = hasSpecialEquipment;
        this.phoneNumber = phoneNumber;
        this.workingHours = workingHours;
    }

    public long getClientId() {
        return clientId;
    }


    public String getWorkingHours() {
        return workingHours;
    }


    public long getStationId() {
        return stationId;
    }


    public String getAddress() {
        return address;
    }


    public int getCapacity() {
        return capacity;
    }

    public int getUrnCount() {
        return urnCount;
    }


    public boolean isHasSpecialEquipment() {
        return hasSpecialEquipment;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setStationId(long stationId) {
        this.stationId = stationId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setUrnCount(int urnCount) {
        this.urnCount = urnCount;
    }

    public void setHasSpecialEquipment(boolean hasSpecialEquipment) {
        this.hasSpecialEquipment = hasSpecialEquipment;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }



    @Override
    public String toString() {
        return "Участок №" + stationId + '\n' +
                "Адрес: " + address + '\n' +
                "Вместимость: " + capacity + '\n' +
                "Количество урн: " + urnCount + '\n' +
                "Наличие специального оборудования: " + (hasSpecialEquipment ? "Да" : "Нет") + '\n' +
                "Номер телефона: " + phoneNumber + '\n' +
                "Часы работы: " + workingHours + '\n';
    }
}
