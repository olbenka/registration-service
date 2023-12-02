package ru.croc.project.pollingStationRegistrationService.entities;

public class RegistrationState {
    private RegistrationStep currentStep = RegistrationStep.ENTER_UNIQUE_NUMBER;
    private String uniqueNumber;
    private String address;
    private int capacity;
    private int urnCount;
    private boolean hasSpecialEquipment;
    private String phoneNumber;
    private String workingHours;

    public RegistrationStep getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(RegistrationStep currentStep) {
        this.currentStep = currentStep;
    }

    public String getUniqueNumber() {
        return uniqueNumber;
    }

    public void setUniqueNumber(String uniqueNumber) {
        this.uniqueNumber = uniqueNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getUrnCount() {
        return urnCount;
    }

    public void setUrnCount(int urnCount) {
        this.urnCount = urnCount;
    }

    public boolean isHasSpecialEquipment() {
        return hasSpecialEquipment;
    }

    public void setHasSpecialEquipment(boolean hasSpecialEquipment) {
        this.hasSpecialEquipment = hasSpecialEquipment;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }
}
