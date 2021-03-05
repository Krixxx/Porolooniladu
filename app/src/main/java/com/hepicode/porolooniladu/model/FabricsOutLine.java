package com.hepicode.porolooniladu.model;

public class FabricsOutLine {

    private int id;
    private String productCode;
    private float quantity;
    private float givenQuantity;
    private String date;
    private String isFilled;
    private String isUrgent;

    public FabricsOutLine() {
    }

    public FabricsOutLine(String productCode, float quantity, float givenQuantity, String date, String isFilled, String isUrgent) {
        this.productCode = productCode;
        this.quantity = quantity;
        this.givenQuantity = givenQuantity;
        this.date = date;
        this.isFilled = isFilled;
        this.isUrgent = isUrgent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getGivenQuantity() {
        return givenQuantity;
    }

    public void setGivenQuantity(float givenQuantity) {
        this.givenQuantity = givenQuantity;
    }

    public String getIsFilled() {
        return isFilled;
    }

    public void setIsFilled(String isFilled) {
        this.isFilled = isFilled;
    }

    public String getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(String isUrgent) {
        this.isUrgent = isUrgent;
    }
}
