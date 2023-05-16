package com.example.swiatzwierzat.model;

import java.util.Base64;

public class Product {
    private Integer id;
    private String name;
    private String description;
    private String productTag;
    private Integer available;
    private Double price;
    private String image;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductTag() {
        return productTag;
    }

    public void setProductTag(String productTag) {
        this.productTag = productTag;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Product(Integer id, String name, String description, String productTag, Integer available, Double price, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.productTag = productTag;
        this.available = available;
        this.price = price;
        this.image = image;
    }
}
