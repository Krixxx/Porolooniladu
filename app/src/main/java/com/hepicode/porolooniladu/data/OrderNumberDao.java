package com.hepicode.porolooniladu.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.hepicode.porolooniladu.model.OrderLine;
import com.hepicode.porolooniladu.model.OrderNumber;

import java.util.List;

@Dao
public interface OrderNumberDao {


    @Insert
    void insertOrderNumber(OrderNumber orderNumber);

    @Delete
    void deleteAOrderNumber(OrderNumber orderNumber);

    @Query("DELETE FROM ordernumber_table")
    void deleteAll();

    @Query("SELECT * FROM ordernumber_table ORDER BY ordernumber_col ASC")
    LiveData<List<OrderNumber>> getAllOrderNumbers();
}
