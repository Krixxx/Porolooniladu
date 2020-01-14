package com.hepicode.porolooniladu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hepicode.porolooniladu.adapter.OrderLineListAdapter;
import com.hepicode.porolooniladu.model.OrderLine;
import com.hepicode.porolooniladu.model.OrderLineViewModel;
import com.hepicode.porolooniladu.util.CalendarModel;

import java.util.ArrayList;
import java.util.List;

public class FoamInActivity extends AppCompatActivity {

    private TextView dateText;
    private Spinner spinner;
    private List<String> spinnerItems = new ArrayList<>();
    private int selection;
    private RecyclerView recyclerView;
    private OrderLineListAdapter orderLineListAdapter;
    private OrderLineViewModel orderLineViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_foam_in);

        Intent intent = getIntent();
        spinnerItems = intent.getStringArrayListExtra("spinner_list");

        orderLineViewModel = ViewModelProviders.of(this).get(OrderLineViewModel.class);

        //Keep screen switched on, when user is working
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        swipeRefreshLayout = findViewById(R.id.refresh_layout);

        //Get current date
        dateText = findViewById(R.id.date_textview);
        CalendarModel cal = new CalendarModel();
        dateText.setText(cal.getCalendarText());



        recyclerView = findViewById(R.id.foam_in_recyclerview);
        orderLineListAdapter = new OrderLineListAdapter(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                loadData(selection);
            }
        });

        recyclerView.setAdapter(orderLineListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Set up spinner
        spinner = findViewById(R.id.spinner);
        adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, spinnerItems);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        adapter.notifyDataSetChanged();

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) adapterView.getChildAt(0)).setTextSize(20);

                selection = Integer.valueOf(spinner.getSelectedItem().toString());

                loadData(selection);


//                orderLineListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void loadData(final int ordernumber) {

        //TODO: find out why observer loads all data (from all previous spinner items), when recyclerviewAdapter refreshes
        orderLineViewModel.getAllUnCheckedSingleOrderLines(ordernumber).observe(this, new Observer<List<OrderLine>>() {
            @Override
            public void onChanged(List<OrderLine> orderLines) {
                //update saved copy of lines

                orderLineListAdapter.setOrderLines(orderLines);

                for (OrderLine line: orderLines){
                    Log.d("ORDERLINE_MAIN", "onChanged: " + line.getOrderNumber());
                    Log.d("ORDERLINE_MAIN", "onChanged: " + line.getProductCode());
                }
            }
        });


    }
}
