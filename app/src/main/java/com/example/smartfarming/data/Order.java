package com.example.smartfarming.data;

public class Order {

    private int id;
    private int restPhone;
    private String restName;
    private String receiverAddr;

    public Order(int id, int restPhone, String restName, String receiverAddr) {
        this.id = id;
        this.restPhone = restPhone;
        this.restName = restName;
        this.receiverAddr = receiverAddr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRestPhone() {
        return restPhone;
    }

    public void setRestPhone(int restPhone) {
        this.restPhone = restPhone;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public String getReceiverAddr() {
        return receiverAddr;
    }

    public void setReceiverAddr(String receiverAddr) {
        this.receiverAddr = receiverAddr;
    }
}
