package com.hepicode.porolooniladu.util;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.hepicode.porolooniladu.data.FoamOutLineDao;
import com.hepicode.porolooniladu.data.FoamOutWeekDao;
import com.hepicode.porolooniladu.data.FoamOutWeekDao_Impl;
import com.hepicode.porolooniladu.data.OrderLineDao;
import com.hepicode.porolooniladu.data.OrderListDatabase;
import com.hepicode.porolooniladu.data.OrderNumberDao;
import com.hepicode.porolooniladu.model.FoamOutLine;
import com.hepicode.porolooniladu.model.FoamOutWeekNumber;
import com.hepicode.porolooniladu.model.OrderLine;
import com.hepicode.porolooniladu.model.OrderNumber;

import java.util.List;

public class OrderLineRepository {

    private OrderLineDao orderLineDao;
    private FoamOutLineDao foamOutLineDao;
    private LiveData<List<OrderLine>> allOrderLines;
    private LiveData<List<FoamOutLine>> allFoamOutLines;
    private LiveData<List<OrderLine>> allCheckedOrderLines;
    private LiveData<List<OrderLine>> allUnCheckedOrderLines;

    private OrderNumberDao orderNumberDao;
    private FoamOutWeekDao weekNumberDao;
    private LiveData<List<OrderNumber>> allOrderNumbers;
    private LiveData<List<FoamOutWeekNumber>> allFoamOutWeekNumbers;


    public OrderLineRepository(Application application) {

        OrderListDatabase db = OrderListDatabase.getDatabase(application);

        orderLineDao = db.orderLineDao();
        foamOutLineDao = db.foamOutLineDao();
        orderNumberDao = db.orderNumberDao();
        weekNumberDao = db.foamOutWeekDao();

        allOrderLines = orderLineDao.getAllOrderLines();
        allFoamOutLines = foamOutLineDao.getAllFoamOutLines();
        allCheckedOrderLines = orderLineDao.getAllCheckedOrderLines();
        allUnCheckedOrderLines = orderLineDao.getAllUnCheckedOrderLines();

        allOrderNumbers = orderNumberDao.getAllOrderNumbers();
        allFoamOutWeekNumbers = weekNumberDao.getAllFoamOutWeekNumbers();

    }

    public LiveData<List<OrderLine>> getAllOrderLines() {

        return allOrderLines;
    }

    public LiveData<List<FoamOutLine>> getAllFoamLines(){

        return allFoamOutLines;
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

    public LiveData<List<OrderLine>> getProblemOrderLines(int orderNumber){
        return orderLineDao.getProblemOrderLines(orderNumber);
    }

    public LiveData<List<OrderLine>> getAllOrderLinesByOrderNumber(int orderNumber){
        return orderLineDao.getAllOrderLinesByOrderNumber(orderNumber);
    }

    public LiveData<List<OrderLine>> getAllCheckedOrderLinesByOrderNumber(int orderNumber){
        return orderLineDao.getAllCheckedOrderLinesByOrderNumber(orderNumber);
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

    public void updateFoamOutLine(FoamOutLine foamOutLine){
        new updateFOLAsyncTask(foamOutLineDao).execute(foamOutLine);
    }

    public void delete(OrderLine orderLine){
        new deleteAsyncTask(orderLineDao).execute(orderLine);
    }

    public void deleteFullOrder(int orderNumber){
        new deleteFullOrderASyncTask(orderLineDao).execute(orderNumber);
    }

    public void deleteFoamOutWeek(String weekNumber){
        new deleteFoamWeekASyncTask(foamOutLineDao).execute(weekNumber);
    }

    public void insertNr(OrderNumber orderNumber){
        new insertNrAsyncTask(orderNumberDao).execute(orderNumber);
    }

    public void deleteNr(OrderNumber orderNumber){
        new deleteNrAsyncTask(orderNumberDao).execute(orderNumber);
    }

    public void deleteFoamOutWeekNr(FoamOutWeekNumber weekNumber){
        new deleteFoamWeekNrASyncTask(weekNumberDao).execute(weekNumber);
    }

    public void insertFoamOutLine(FoamOutLine foamOutLine){
        new insertFoamOutLineAsyncTask(foamOutLineDao).execute(foamOutLine);
    }

    public LiveData<List<FoamOutLine>> getAllUncheckedSingleFoamOutLines(String weekNumber){
        Log.d("FOAM_OUT_REPOSITORY", "getAllUncheckedSingleFoamOutLines: " + weekNumber);
        return foamOutLineDao.getAllUncheckedSingleFoamOutLines(weekNumber);
    }

    public LiveData<List<FoamOutWeekNumber>> getAllFoamOutWeekNumbers(){

        return allFoamOutWeekNumbers;

    }

    public void insertFoamOutWeekNr(FoamOutWeekNumber weekNumber){
        new insertFoamOutWeekNrAsyncTask(weekNumberDao).execute(weekNumber);
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

    private class updateFOLAsyncTask extends AsyncTask<FoamOutLine, Void, Void>{

        private FoamOutLineDao asyncTackDao;

        public updateFOLAsyncTask(FoamOutLineDao dao){

            asyncTackDao = dao;
        }

        @Override
        protected Void doInBackground(FoamOutLine... params) {

            asyncTackDao.updateFoamOutLine(params[0]); // OrderLine... params will give us a list, therefore we insert first position (0)

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

    private static class deleteFullOrderASyncTask extends AsyncTask<Integer, Void, Void>{

        private OrderLineDao asyncTaskDao;

        deleteFullOrderASyncTask(OrderLineDao dao){
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            asyncTaskDao.deleteFullOrder(params[0]);
            return null;
        }
    }

    private static class deleteFoamWeekASyncTask extends AsyncTask<String, Void, Void>{

        private FoamOutLineDao asyncTaskDao;

        deleteFoamWeekASyncTask(FoamOutLineDao dao){
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(String... params) {
            asyncTaskDao.deleteFullWeek(params[0]);
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

    private static class deleteFoamWeekNrASyncTask extends AsyncTask<FoamOutWeekNumber, Void, Void>{

        private FoamOutWeekDao asyncTaskDao;

        deleteFoamWeekNrASyncTask(FoamOutWeekDao dao){
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(FoamOutWeekNumber... params) {
            asyncTaskDao.deleteAWeekNumber(params[0]);
            return null;
        }
    }

    private class insertFoamOutLineAsyncTask extends AsyncTask<FoamOutLine, Void, Void> {

        private FoamOutLineDao asyncTaskDao;

        public insertFoamOutLineAsyncTask(FoamOutLineDao dao) {

            asyncTaskDao = dao;

        }

        @Override
        protected Void doInBackground(FoamOutLine... params) {

            asyncTaskDao.insert(params[0]); // FoamOutLine... params will give us a list, therefore we insert first position (0)

            return null;
        }
    }

    private class insertFoamOutWeekNrAsyncTask extends AsyncTask<FoamOutWeekNumber, Void, Void> {

        private FoamOutWeekDao asyncTaskDao;

        public insertFoamOutWeekNrAsyncTask(FoamOutWeekDao dao) {

            asyncTaskDao = dao;

        }

        @Override
        protected Void doInBackground(FoamOutWeekNumber... params) {

            asyncTaskDao.insertWeekNumber(params[0]); // FoamOutWeekNumber... params will give us a list, therefore we insert first position (0)

            return null;
        }
    }

}
