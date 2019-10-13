package com.example.ahmad2.shopproject;

public class Information {

    private Long id;
    private int version;
    private String message;
    private short seen;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public short getSeen() {
        return seen;
    }

    public void setSeen(short seen) {
        this.seen = seen;
    }
}
