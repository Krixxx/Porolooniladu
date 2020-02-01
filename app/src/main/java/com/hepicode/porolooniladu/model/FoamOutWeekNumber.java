package com.hepicode.porolooniladu.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "foam_out_week_number_table")
public class FoamOutWeekNumber {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "week_number_col")
    private String weekNumber;

    public FoamOutWeekNumber(String weekNumber) {
        this.weekNumber = weekNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(String weekNumber) {
        this.weekNumber = weekNumber;
    }
}
