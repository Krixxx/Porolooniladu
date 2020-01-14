package com.hepicode.porolooniladu.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "ordernumber_table")
public class OrderNumber {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "ordernumber_col")
    private int orderNumber;

    public OrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
}
