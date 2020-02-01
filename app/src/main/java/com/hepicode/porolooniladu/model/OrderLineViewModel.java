package com.hepicode.porolooniladu.model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.hepicode.porolooniladu.FoamInActivity;
import com.hepicode.porolooniladu.util.OrderLineRepository;

import java.util.List;

public class OrderLineViewModel extends AndroidViewModel {

    private OrderLineRepository orderLineRepository;
    private LiveData<List<OrderLine>> allOrderLines;
    private LiveData<List<FoamOutLine>> allFoamOutLines;
    private LiveData<List<OrderLine>> allCheckedOrderLines;
    private LiveData<List<OrderLine>> allUnCheckedOrderLines;
    private LiveData<List<OrderLine>> getAllLines;
    private LiveData<List<FoamOutLine>> getAllFoamOutLines;
    private final MutableLiveData<Integer> orderLineFilter;
    private final MutableLiveData<String> weekOutFilter;

    private LiveData<List<OrderNumber>> allOrderNumbers;
    private LiveData<List<FoamOutWeekNumber>> allWeekNumbers;


    public OrderLineViewModel(@NonNull Application application) {
        super(application);
        orderLineRepository = new OrderLineRepository(application);
        allOrderLines = orderLineRepository.getAllOrderLines();
        allFoamOutLines = orderLineRepository.getAllFoamLines();
        allCheckedOrderLines = orderLineRepository.getAllCheckedOrderLines();
        allUnCheckedOrderLines = orderLineRepository.getAllUnCheckedOrderLines();
        orderLineFilter = new MutableLiveData<>();
        weekOutFilter = new MutableLiveData<>();

        allOrderNumbers = orderLineRepository.getAllOrderNumbers();
        allWeekNumbers = orderLineRepository.getAllFoamOutWeekNumbers();
    }

    public LiveData<List<OrderLine>> getAllOrderLines(){
        return allOrderLines;
    }

    public LiveData<List<FoamOutLine>> getAllFoamLines(){
        return allFoamOutLines;
    }

    public LiveData<List<OrderLine>> getAllCheckedOrderLines(){
        return allCheckedOrderLines;
    }

    public LiveData<List<OrderLine>> getAllUnCheckedOrderLines(){
        return allUnCheckedOrderLines;
    }


    //This part changed, to get correct data to recyclerview.

    public void setOrderLineFilter(int orderNumber){
        orderLineFilter.setValue(orderNumber);
    }

    public void setFoamOutFilter(String weekNumber){
        weekOutFilter.setValue(weekNumber);
        Log.d("FOAM_OUT_MODEL_FILTER", "setFoamOutFilter: " + weekNumber);
    }

    public LiveData<OrderLine> getAOrderLine(int id){
        return orderLineRepository.getAOrderLine(id);
    }

    public LiveData<List<OrderLine>> getProblemOrderLines(int orderNumber){
        return orderLineRepository.getProblemOrderLines(orderNumber);
    }

    public LiveData<List<OrderLine>> getAllOrderLinesByOrderNumber(int orderNumber){
        return orderLineRepository.getAllOrderLinesByOrderNumber(orderNumber);
    }

    public LiveData<List<OrderLine>> getAllCheckedOrderLinesByOrderNumber(int orderNumber){
        return orderLineRepository.getAllCheckedOrderLinesByOrderNumber(orderNumber);
    }

    public LiveData<List<OrderLine>> getAllUnCheckedSingleOrderLines(int order){
        return getAllLines = Transformations.switchMap(orderLineFilter, orderNumber -> orderLineRepository.getAllUnCheckedSingleOrderLines(orderNumber));
    }

    public LiveData<List<FoamOutLine>> getAllUncheckedSingleWeekLines(String week){
        Log.d("FOAM_OUT_VIEWMODEL", "getAllUncheckedSingleWeekLines: " + week);
        return getAllFoamOutLines = Transformations.switchMap(weekOutFilter, weekNumber -> orderLineRepository.getAllUncheckedSingleFoamOutLines(weekNumber));
    }

    public LiveData<List<OrderNumber>> getAllOrderNumbers(){
        return allOrderNumbers;
    }

    public LiveData<List<FoamOutWeekNumber>> getAllWeekNumbers(){
        return allWeekNumbers;
    }

    public void insert(OrderLine orderLine){
        orderLineRepository.insert(orderLine);
    }

    public void update(OrderLine orderLine){
        orderLineRepository.update(orderLine);
    }

    public void updateFoamOutLine(FoamOutLine foamOutLine){
        orderLineRepository.updateFoamOutLine(foamOutLine);
    }

    public void deleteAOrderLine(OrderLine orderLine){
        orderLineRepository.delete(orderLine);
    }

    public void deleteFullOrder(Integer orderNumber){
        orderLineRepository.deleteFullOrder(orderNumber);
    }

    public void deleteFoamOutWeek(String weekNumber){
        orderLineRepository.deleteFoamOutWeek(weekNumber);
    }

    public void insertNr(OrderNumber orderNumber){
        orderLineRepository.insertNr(orderNumber);
    }

    public void deleteAOrderNumber(OrderNumber orderNumber){
        orderLineRepository.deleteNr(orderNumber);
    }

    public void deleteFoamOutWeekNr(FoamOutWeekNumber weekNumber){
        orderLineRepository.deleteFoamOutWeekNr(weekNumber);
    }

    public void insertFoamOutLine(FoamOutLine foamOutLine){
        orderLineRepository.insertFoamOutLine(foamOutLine);
    }

    public void insertFoamOutWeekNumber(FoamOutWeekNumber weekNumber){
        orderLineRepository.insertFoamOutWeekNr(weekNumber);
    }
}
