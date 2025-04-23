package com.marsys.marsys.Models;

public class Employee {
    private String firstName;
    private String lastName;
    private String position;
    private String id;
    private String password;
    private String storeCode;

    public Employee(String firstName, String lastName, String position, String id, String password, String storeCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.id = id;
        this.password = password;
        this.storeCode = storeCode;
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
}
