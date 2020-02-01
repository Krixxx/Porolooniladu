package com.hepicode.porolooniladu.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.hepicode.porolooniladu.model.FoamOutWeekNumber;
import com.hepicode.porolooniladu.model.OrderNumber;

import java.util.List;

@Dao
public interface FoamOutWeekDao {

    @Insert
    void insertWeekNumber(FoamOutWeekNumber weekNumber);

    @Delete
    void deleteAWeekNumber(FoamOutWeekNumber weekNumber);

    @Query("DELETE FROM foam_out_week_number_table")
    void deleteAllWeekNumbers();

    @Query("SELECT * FROM foam_out_week_number_table ORDER BY week_number_col ASC")
    LiveData<List<FoamOutWeekNumber>> getAllFoamOutWeekNumbers();


}
