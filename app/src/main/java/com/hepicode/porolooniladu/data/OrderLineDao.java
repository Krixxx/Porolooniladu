package com.hepicode.porolooniladu.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hepicode.porolooniladu.model.OrderLine;

import java.util.List;

@Dao
public interface OrderLineDao {

    //CRUD

    @Insert
    void insert(OrderLine orderLine);

    @Query("DELETE FROM orderline_table")
    void deleteAll();

    @Delete
    void deleteAOrderLine(OrderLine orderLine);

    @Query("UPDATE orderline_table SET productcode_col = :productCode AND orderedqty_col = :orderedQuantity AND arrivedqty_col = :arrivedQuantity AND isarrived_col = :isArrived AND ordernumber_col = :orderNumber WHERE id = :id")
    int updateOrderLineItem(int id, String productCode, int orderedQuantity, int arrivedQuantity, int isArrived, int orderNumber);

    @Update
    void updateOrderLine(OrderLine orderLine);

    @Query("SELECT * FROM orderline_table WHERE (isarrived_col = 2 OR isarrived_col = 3) AND ordernumber_col = :orderNumber ORDER BY productcode_col ASC")
    LiveData<List<OrderLine>> getProblemOrderLines(int orderNumber);

    @Query("SELECT * FROM orderline_table WHERE id = :id")
    LiveData<OrderLine> getAOrderLine(int id);

    @Query("SELECT * FROM orderline_table ORDER BY productcode_col ASC")
    LiveData<List<OrderLine>> getAllOrderLines();

    @Query("SELECT * FROM orderline_table WHERE ordernumber_col = :orderNumber")
    LiveData<List<OrderLine>> getAllOrderLinesByOrderNumber(int orderNumber);

    @Query("SELECT * FROM orderline_table WHERE isarrived_col = 1 AND ordernumber_col = :orderNumber")
    LiveData<List<OrderLine>> getAllCheckedOrderLinesByOrderNumber(int orderNumber);

    @Query("SELECT * FROM orderline_table WHERE isarrived_col = 1 ORDER BY ordernumber_col ASC, productcode_col ASC ")
    LiveData<List<OrderLine>> getAllCheckedOrderLines();

    @Query("SELECT * FROM orderline_table WHERE isarrived_col = 0 ORDER BY ordernumber_col ASC, productcode_col ASC ")
    LiveData<List<OrderLine>> getAllUnCheckedOrderLines();

    @Query("SELECT * FROM orderline_table WHERE (isarrived_col = 0 OR isarrived_col = 2 OR isarrived_col = 3) AND ordernumber_col = :orderNumber ORDER BY productcode_col ASC ")
    LiveData<List<OrderLine>> getAllUnCheckedSingleOrderLines(int orderNumber);



}
