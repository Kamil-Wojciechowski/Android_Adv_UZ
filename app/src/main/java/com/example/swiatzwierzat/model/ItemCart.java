package com.example.swiatzwierzat.model;

public class ItemCart {
    private Integer id;
    private String name;
    private Integer amount;
    private Integer available;

    public ItemCart(Integer id, String name, Integer amount, Integer available) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.available = available;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }
}
