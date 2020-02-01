package com.hepicode.porolooniladu.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hepicode.porolooniladu.model.FoamOutLine;

import java.util.List;

@Dao
public interface FoamOutLineDao {

    @Insert
    void insert(FoamOutLine foamOutLine);

    @Query("DELETE FROM foam_out_table")
    void deleteAll();

    @Update
    void updateFoamOutLine(FoamOutLine foamOutLine);

    @Delete
    void deleteAFoamOutLine(FoamOutLine foamOutLine);

    @Query("SELECT * FROM foam_out_table")
    LiveData<List<FoamOutLine>> getAllFoamOutLines();

    @Query("DELETE FROM foam_out_table WHERE weeknumber_col = :weekNumber")
    void deleteFullWeek(String weekNumber);

    @Query("Select * FROM foam_out_table WHERE isgivenout_col = 1 AND weeknumber_col = :weekNumber ORDER BY date(dateout_col) ASC")
    LiveData<List<FoamOutLine>> getAllGivenOutFoamOutLines(String weekNumber);

    @Query("SELECT * FROM foam_out_table WHERE isgivenout_col = 0 AND weeknumber_col = :weekNumber ORDER BY date(dateout_col) ASC")
    LiveData<List<FoamOutLine>> getAllUncheckedSingleFoamOutLines(String weekNumber);
}
