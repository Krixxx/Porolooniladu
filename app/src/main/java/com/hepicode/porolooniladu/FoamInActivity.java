package com.hepicode.porolooniladu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hepicode.porolooniladu.adapter.OrderLineListAdapter;
import com.hepicode.porolooniladu.model.OrderLine;
import com.hepicode.porolooniladu.model.OrderLineViewModel;
import com.hepicode.porolooniladu.util.CalendarModel;
import com.hepicode.porolooniladu.util.FoamInBottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class FoamInActivity extends AppCompatActivity implements FoamInBottomSheetDialog.BottomSheetListener {

    private TextView dateText;
    private Spinner spinner;
    private List<String> spinnerItems = new ArrayList<>();
    private int selection;
    private RecyclerView recyclerView;
    private OrderLineListAdapter orderLineListAdapter;
    private OrderLineViewModel orderLineViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayAdapter<String> adapter;
    private SearchView searchView;
    private OrderLine mOrderLine;
    private DividerItemDecoration dividerItemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_foam_in);

        Intent intent = getIntent();
        spinnerItems = intent.getStringArrayListExtra("spinner_list");

        initViews();

        orderLineViewModel = ViewModelProviders.of(this).get(OrderLineViewModel.class);

        //Keep screen switched on, when user is working
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Get current date - taken out of app.

//        CalendarModel cal = new CalendarModel();
//        dateText.setText(cal.getCalendarText());



        //Load first spinner item data to recyclerview
        if (spinnerItems != null){
            loadData(Integer.valueOf(spinnerItems.get(0)));
        }else{
            finish();
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                loadData(selection);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                    orderLineListAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                    orderLineListAdapter.getFilter().filter(s);
                return false;
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) adapterView.getChildAt(0)).setTextSize(20);

                selection = Integer.valueOf(spinner.getSelectedItem().toString());

                orderLineViewModel.setOrderLineFilter(selection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void initViews(){

        swipeRefreshLayout = findViewById(R.id.refresh_layout);
        searchView = findViewById(R.id.foam_in_searchview);
        dateText = findViewById(R.id.date_textview);

        recyclerView = findViewById(R.id.foam_in_recyclerview);

        orderLineListAdapter = new OrderLineListAdapter(this);



        spinner = findViewById(R.id.spinner);
        adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, spinnerItems);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        adapter.notifyDataSetChanged();

        spinner.setAdapter(adapter);

        recyclerView.setAdapter(orderLineListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void loadData(final int ordernumber) {

        orderLineViewModel.getAllUnCheckedSingleOrderLines(ordernumber).observe(this, new Observer<List<OrderLine>>() {
            @Override
            public void onChanged(List<OrderLine> orderLines) {
                //update saved copy of lines

                orderLineListAdapter.setOrderLines(orderLines);
                String lines = getString(R.string.lines_title)+ " " + orderLineListAdapter.getItemCount();
                dateText.setText(lines);

//                for (OrderLine line: orderLines){
////                    Log.d("ORDERLINE_MAIN", "onChanged: " + line.getOrderNumber());
////
////                    Log.d("ORDERLINE_MAIN", "onChanged: " + line.getProductCode());
////                }
            }
        });
    }

    @Override
    public void onButtonClicked(int id, int isArrived, int quantity, int orderNumber, OrderLine line) {

        if (isArrived == 0 || quantity == 0){

            Toast.makeText(this, "Palun täida vajalikud väljad!", Toast.LENGTH_SHORT).show();

        }else {

            mOrderLine = line;

            if (mOrderLine == null){

                Log.d("MAIN_ORDERLINE_NULL", "onButtonClicked: ei lae andmeid");

            }else {

                mOrderLine.setArrivedQuantity(quantity);
                mOrderLine.setIsArrived(isArrived);
                orderLineViewModel.update(mOrderLine);
                orderLineListAdapter.notifyDataSetChanged();
                Log.d("MAIN", "onChanged: " + mOrderLine.getProductCode());
            }
        }



    }
}
