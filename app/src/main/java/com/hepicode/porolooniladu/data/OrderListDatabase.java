package com.hepicode.porolooniladu.data;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.hepicode.porolooniladu.model.FoamOutLine;
import com.hepicode.porolooniladu.model.FoamOutWeekNumber;
import com.hepicode.porolooniladu.model.OrderLine;
import com.hepicode.porolooniladu.model.OrderNumber;

@Database(entities = {OrderLine.class, OrderNumber.class, FoamOutLine.class, FoamOutWeekNumber.class}, version = 3, exportSchema = false)
public abstract class OrderListDatabase extends RoomDatabase {

    public static volatile OrderListDatabase INSTANCE;

    public abstract OrderLineDao orderLineDao();
    public abstract OrderNumberDao orderNumberDao();
    public abstract FoamOutLineDao foamOutLineDao();
    public abstract FoamOutWeekDao foamOutWeekDao();

    public static OrderListDatabase getDatabase(final Context context) {

        if (INSTANCE == null){

            synchronized (OrderListDatabase.class){

                if (INSTANCE == null){

                    // create our db
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            OrderListDatabase.class, "orderline_database")
                            .addCallback(roomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final OrderLineDao orderLineDao;
        private final OrderNumberDao orderNumberDao;
        private final FoamOutLineDao foamOutLineDao;
        private final FoamOutWeekDao foamOutWeekDao;

        public PopulateDbAsync(OrderListDatabase db) {
            orderLineDao = db.orderLineDao();
            orderNumberDao = db.orderNumberDao();
            foamOutLineDao = db.foamOutLineDao();
            foamOutWeekDao = db.foamOutWeekDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {


//            orderLineDao.deleteAll();
//            orderNumberDao.deleteAll();
//            foamOutLineDao.deleteAll();
//            foamOutWeekDao.deleteAllWeekNumbers();

            return null;
        }
    }

}
