package com.hepicode.porolooniladu.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "orderline_table")
public class OrderLine {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "productcode_col")
    private String productCode;

    @ColumnInfo(name = "orderedqty_col")
    private int orderedQuantity;

    @ColumnInfo(name = "arrivedqty_col")
    private int arrivedQuantity;

    @ColumnInfo(name = "isarrived_col")
    private int isArrived;  // isArrived has 4 statuses: 0 - not arrived, 1 - arrived, 2 - partially arrived, 3 -  some details missing

    @ColumnInfo(name = "ordernumber_col")
    private int orderNumber;

    public OrderLine(@NonNull String productCode, int orderedQuantity, int arrivedQuantity, int isArrived, int orderNumber) {
        this.productCode = productCode;
        this.orderedQuantity = orderedQuantity;
        this.arrivedQuantity = arrivedQuantity;
        this.isArrived = isArrived;
        this.orderNumber = orderNumber;
    }

    public int getId() {
        return id;
    }

    public String getProductCode() {
        return productCode;
    }

    public int getOrderedQuantity() {
        return orderedQuantity;
    }

    public int getArrivedQuantity() {
        return arrivedQuantity;
    }

    public int getIsArrived() {
        return isArrived;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProductCode(@NonNull String productCode) {
        this.productCode = productCode;
    }

    public void setOrderedQuantity(int orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    public void setArrivedQuantity(int arrivedQuantity) {
        this.arrivedQuantity = arrivedQuantity;
    }

    public void setIsArrived(int isArrived) {
        this.isArrived = isArrived;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
}
