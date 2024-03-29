package com.hepicode.porolooniladu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hepicode.porolooniladu.adapter.FoamOutListAdapter;
import com.hepicode.porolooniladu.model.FoamOutLine;
import com.hepicode.porolooniladu.model.OrderLineViewModel;
import com.hepicode.porolooniladu.util.CalendarModel;
import com.hepicode.porolooniladu.util.FoamOutBottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class FoamOutActivity extends AppCompatActivity implements FoamOutBottomSheetDialog.BottomSheetListener {


    private List<String> spinnerItems = new ArrayList<>();
    private String selection;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView dateText, linesText;
    private RecyclerView recyclerView;
    private Spinner spinner;
    private DividerItemDecoration dividerItemDecoration;
    private ArrayAdapter<String> spinnerAdapter;
    private FoamOutListAdapter foamOutListAdapter;
    private OrderLineViewModel orderLineViewModel;
    private FoamOutLine mFoamOutLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_foam_out);

        spinnerItems = getIntent().getStringArrayListExtra("spinner_list");

        Log.d("FOAM_OUT_MAIN", "onCreate: " + spinnerItems.size());

        initViews();

        orderLineViewModel = ViewModelProviders.of(this).get(OrderLineViewModel.class);

        CalendarModel cal = new CalendarModel();
        dateText.setText(cal.getCalendarText());

        if (spinnerItems != null) {
            loadData(spinnerItems.get(0));
        } else {
            finish();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                loadData(selection);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) adapterView.getChildAt(0)).setTextSize(20);

                selection = spinner.getSelectedItem().toString();

                orderLineViewModel.setFoamOutFilter(selection);

//                loadReportOrderLines(selection);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

    }

    private void initViews(){

        swipeRefreshLayout = findViewById(R.id.foam_out_refresh_layout);
        dateText = findViewById(R.id.foam_out_current_date_textview);
        linesText = findViewById(R.id.foam_out_lines_textview);
        recyclerView = findViewById(R.id.foam_out_recyclerview);

        foamOutListAdapter = new FoamOutListAdapter(this);

        spinner = findViewById(R.id.foam_out_spinner);
        spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, spinnerItems);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);

        spinnerAdapter.notifyDataSetChanged();

        spinner.setAdapter(spinnerAdapter);

        recyclerView.setAdapter(foamOutListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


    }

    private void loadData(final String weekNumber){

        Log.d("FOAM_OUT_LIST_SIZE_MAIN", "onChanged: " + weekNumber);

        orderLineViewModel.getAllUncheckedSingleWeekLines(weekNumber).observe(this, new Observer<List<FoamOutLine>>() {
            @Override
            public void onChanged(List<FoamOutLine> foamOutLines) {

                foamOutListAdapter.setFoamOutLinesList(foamOutLines);
                String lines = getString(R.string.lines_title) + " " + foamOutListAdapter.getItemCount();
                linesText.setText(lines);

            }
        });

    }


    @Override
    public void onButtonClicked(int id, int isGivenOut, int partialQuantity, String weekNumber, FoamOutLine line) {

        mFoamOutLine = line;

        if (mFoamOutLine != null) {
            if (mFoamOutLine.getPartialQuantity() < mFoamOutLine.getOutQuantity() && mFoamOutLine.getPartialQuantity() > 0){
                mFoamOutLine.setIsGivenOut(2);
            } else if (mFoamOutLine.getPartialQuantity() == mFoamOutLine.getOutQuantity()){
                mFoamOutLine.setIsGivenOut(1);
            } else if (mFoamOutLine.getPartialQuantity() > mFoamOutLine.getOutQuantity()){
                Toast.makeText(this, "Kogus ei saa olla suurem kui plaanitud!", Toast.LENGTH_SHORT).show();
                mFoamOutLine.setPartialQuantity(0);
            } else if (mFoamOutLine.getPartialQuantity() == 0){
                Toast.makeText(this, "Kogus on 0", Toast.LENGTH_SHORT).show();
            }
        }

        orderLineViewModel.updateFoamOutLine(mFoamOutLine);
        foamOutListAdapter.notifyDataSetChanged();

//        if (mFoamOutLine == null) {
//
//            Log.d("MAIN_ORDERLINE_NULL", "onButtonClicked: ei lae andmeid");
//
//        } else {
//
//            mFoamOutLine.setPartialQuantity(partialQuantity);
//            mFoamOutLine.setIsGivenOut(isGivenOut);
//            orderLineViewModel.updateFoamOutLine(mFoamOutLine);
//            foamOutListAdapter.notifyDataSetChanged();
//            Log.d("MAIN", "onChanged: " + mFoamOutLine.getProductCode());
//        }
        
    }
}
