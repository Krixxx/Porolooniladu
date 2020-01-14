package com.hepicode.porolooniladu.model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hepicode.porolooniladu.util.OrderLineRepository;

import java.util.List;

public class OrderLineViewModel extends AndroidViewModel {

    private OrderLineRepository orderLineRepository;
    private LiveData<List<OrderLine>> allOrderLines;
    private LiveData<List<OrderLine>> allCheckedOrderLines;
    private LiveData<List<OrderLine>> allUnCheckedOrderLines;

    private LiveData<List<OrderNumber>> allOrderNumbers;


    public OrderLineViewModel(@NonNull Application application) {
        super(application);
        orderLineRepository = new OrderLineRepository(application);
        allOrderLines = orderLineRepository.getAllOrderLines();
        allCheckedOrderLines = orderLineRepository.getAllCheckedOrderLines();
        allUnCheckedOrderLines = orderLineRepository.getAllUnCheckedOrderLines();

        allOrderNumbers = orderLineRepository.getAllOrderNumbers();
    }

    public LiveData<List<OrderLine>> getAllOrderLines(){
        return allOrderLines;
    }

    public LiveData<List<OrderLine>> getAllCheckedOrderLines(){
        return allCheckedOrderLines;
    }

    public LiveData<List<OrderLine>> getAllUnCheckedOrderLines(){
        return allUnCheckedOrderLines;
    }

    public LiveData<List<OrderLine>> getAllUnCheckedSingleOrderLines(int orderNumber){
        return orderLineRepository.getAllUnCheckedSingleOrderLines(orderNumber);
    }

    public LiveData<List<OrderNumber>> getAllOrderNumbers(){
        return allOrderNumbers;
    }

    public void insert(OrderLine orderLine){
        orderLineRepository.insert(orderLine);
    }

    public void update(OrderLine orderLine){
        orderLineRepository.update(orderLine);
    }

    public void deleteAOrderLine(OrderLine orderLine){
        orderLineRepository.delete(orderLine);
    }

    public void insertNr(OrderNumber orderNumber){
        orderLineRepository.insertNr(orderNumber);
    }

    public void deleteAOrderNr(OrderNumber orderNumber){
        orderLineRepository.deleteNr(orderNumber);
    }
}