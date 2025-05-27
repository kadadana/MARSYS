package com.marsys.marsys.Models;

public class Employee {
    private String firstName;
    private String lastName;
    private String position;
    private String id;
    private String password;
    private String storeCode;
    private String startDate;
    private String endDate;
    private String birthDate;

    public Employee(String firstName, String lastName, String position,
                    String id, String password, String storeCode, String startDate,
                    String endDate, String birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.id = id;
        this.password = password;
        this.storeCode = storeCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.birthDate = birthDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate() {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}
