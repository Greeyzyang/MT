package com.example.connectapplication.event;

public class BindEvent {

    public String message;

    public String errorinfo;

    public BindEvent(String message,String errorinfo) {
        this.message = message;
        this.errorinfo = errorinfo;
    }

}
