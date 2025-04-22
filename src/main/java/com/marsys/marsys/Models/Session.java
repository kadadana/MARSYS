package com.marsys.marsys.Models;

public class Session {
    private static Session instance;
    private Employee currentUser;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public Employee getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Employee user) {
        this.currentUser = user;
    }

    public void logout() {
        currentUser = null;
    }
}