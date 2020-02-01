package com.hepicode.porolooniladu.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "foam_out_table")
public class FoamOutLine {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "productcode_col")
    private String productCode;

    @ColumnInfo(name = "outqty_col")
    private int outQuantity;

    @ColumnInfo(name = "dateout_col")
    private String dateOut;

    @ColumnInfo(name = "isgivenout_col")
    private int isGivenOut;

    @ColumnInfo(name = "workernumber_col")
    private int workerNumber;

    @ColumnInfo(name = "weeknumber_col")
    private String weekNumber;

    public FoamOutLine(@NonNull String productCode, int outQuantity, String dateOut, int isGivenOut, int workerNumber, String weekNumber) {
        this.productCode = productCode;
        this.outQuantity = outQuantity;
        this.dateOut = dateOut;
        this.isGivenOut = isGivenOut;
        this.workerNumber = workerNumber;
        this.weekNumber = weekNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(@NonNull String productCode) {
        this.productCode = productCode;
    }

    public int getOutQuantity() {
        return outQuantity;
    }

    public void setOutQuantity(int outQuantity) {
        this.outQuantity = outQuantity;
    }

    public String getDateOut() {
        return dateOut;
    }

    public void setDateOut(String dateOut) {
        this.dateOut = dateOut;
    }

    public int getIsGivenOut() {
        return isGivenOut;
    }

    public void setIsGivenOut(int isGivenOut) {
        this.isGivenOut = isGivenOut;
    }

    public int getWorkerNumber() {
        return workerNumber;
    }

    public void setWorkerNumber(int workerNumber) {
        this.workerNumber = workerNumber;
    }

    public String getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(String weekNumber) {
        this.weekNumber = weekNumber;
    }
}
