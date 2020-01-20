package com.hepicode.porolooniladu.util;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.hepicode.porolooniladu.data.OrderLineDao;
import com.hepicode.porolooniladu.data.OrderListDatabase;
import com.hepicode.porolooniladu.data.OrderNumberDao;
import com.hepicode.porolooniladu.model.OrderLine;
import com.hepicode.porolooniladu.model.OrderNumber;

import java.util.List;

public class OrderLineRepository {

    private OrderLineDao orderLineDao;
    private LiveData<List<OrderLine>> allOrderLines;
    private LiveData<List<OrderLine>> allCheckedOrderLines;
    private LiveData<List<OrderLine>> allUnCheckedOrderLines;

    private OrderNumberDao orderNumberDao;
    private LiveData<List<OrderNumber>> allOrderNumbers;


    public OrderLineRepository(Application application) {

        OrderListDatabase db = OrderListDatabase.getDatabase(application);

        orderLineDao = db.orderLineDao();
        orderNumberDao = db.orderNumberDao();

        allOrderLines = orderLineDao.getAllOrderLines();
        allCheckedOrderLines = orderLineDao.getAllCheckedOrderLines();
        allUnCheckedOrderLines = orderLineDao.getAllUnCheckedOrderLines();

        allOrderNumbers = orderNumberDao.getAllOrderNumbers();
    }

    public LiveData<List<OrderLine>> getAllOrderLines() {

        return allOrderLines;
    }

    public LiveData<List<OrderLine>> getAllCheckedOrderLines() {

        return allCheckedOrderLines;
    }

    public LiveData<OrderLine> getAOrderLine(int id){

        return orderLineDao.getAOrderLine(id);
    }

    public LiveData<List<OrderLine>> getAllUnCheckedOrderLines() {

        return allUnCheckedOrderLines;
    }

    public LiveData<List<OrderLine>> getAllUnCheckedSingleOrderLines(int orderNumber) {
        return orderLineDao.getAllUnCheckedSingleOrderLines(orderNumber);
    }

    public LiveData<List<OrderNumber>> getAllOrderNumbers(){

        return allOrderNumbers;
    }

    public void insert(OrderLine orderLine){
        new insertAsyncTask(orderLineDao).execute(orderLine);
    }

    public void update(OrderLine orderLine){
        new updateAsyncTask(orderLineDao).execute(orderLine);
        Log.d("ORDERLINE_REPOSITORY", "updateOrderLine: " + orderLine.getOrderNumber() + " product: "
                + orderLine.getProductCode() + " qty: " + orderLine.getArrivedQuantity()
                + " " + orderLine.getIsArrived() + " " + orderLine.getId());
    }

    public void delete(OrderLine orderLine){
        new deleteAsyncTask(orderLineDao).execute(orderLine);
    }

    public void insertNr(OrderNumber orderNumber){
        new insertNrAsyncTask(orderNumberDao).execute(orderNumber);
    }

    public void deleteNr(OrderNumber orderNumber){
        new deleteNrAsyncTask(orderNumberDao).execute(orderNumber);
    }


    private class insertAsyncTask extends AsyncTask<OrderLine, Void, Void> {

        private OrderLineDao asyncTaskDao;

        public insertAsyncTask(OrderLineDao dao) {

            asyncTaskDao = dao;

        }

        @Override
        protected Void doInBackground(OrderLine... params) {

            asyncTaskDao.insert(params[0]); // OrderLine... params will give us a list, therefore we insert first position (0)

            return null;
        }
    }

    private class updateAsyncTask extends AsyncTask<OrderLine, Void, Void> {

        private OrderLineDao asyncTaskDao;

        public updateAsyncTask(OrderLineDao dao) {

            asyncTaskDao = dao;

        }

        @Override
        protected Void doInBackground(OrderLine... params) {

            asyncTaskDao.updateOrderLine(params[0]); // OrderLine... params will give us a list, therefore we insert first position (0)

            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<OrderLine, Void, Void> {

        private OrderLineDao asyncTaskDao;

        deleteAsyncTask(OrderLineDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final OrderLine... params) {
            asyncTaskDao.deleteAOrderLine(params[0]);
            return null;
        }
    }

    private class insertNrAsyncTask extends AsyncTask<OrderNumber, Void, Void> {

        private OrderNumberDao asyncTaskDao;

        public insertNrAsyncTask(OrderNumberDao dao) {

            asyncTaskDao = dao;

        }

        @Override
        protected Void doInBackground(OrderNumber... params) {

            asyncTaskDao.insertOrderNumber(params[0]); // OrderLine... params will give us a list, therefore we insert first position (0)

            return null;
        }
    }

    private static class deleteNrAsyncTask extends AsyncTask<OrderNumber, Void, Void> {

        private OrderNumberDao asyncTaskDao;

        deleteNrAsyncTask(OrderNumberDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final OrderNumber... params) {
            asyncTaskDao.deleteAOrderNumber(params[0]);
            return null;
        }
    }
}
