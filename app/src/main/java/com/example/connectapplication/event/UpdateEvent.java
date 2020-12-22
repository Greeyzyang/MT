package com.example.connectapplication.event;

public class UpdateEvent {

    public String message;

    public int time;

    public UpdateEvent(String message, int time) {
        this.message = message;
        this.time = time;
    }

}
