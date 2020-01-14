package com.hepicode.porolooniladu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import com.hepicode.porolooniladu.adapter.OrderLineDeletedListAdapter;
import com.hepicode.porolooniladu.adapter.OrderLineListAdapter;
import com.hepicode.porolooniladu.model.OrderLine;
import com.hepicode.porolooniladu.model.OrderLineViewModel;

import java.util.List;

public class FoamInDeletedActivity extends AppCompatActivity {

    private String selection;
    private RecyclerView recyclerView;
    private OrderLineDeletedListAdapter orderLineDeletedListAdapter;
    private OrderLineViewModel orderLineViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_foam_in_deleted);

        orderLineViewModel = ViewModelProviders.of(this).get(OrderLineViewModel.class);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        swipeRefreshLayout = findViewById(R.id.refresh_layout_del);

        recyclerView = findViewById(R.id.foam_in_recyclerview_del);
        orderLineDeletedListAdapter = new OrderLineDeletedListAdapter(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                loadData();
            }
        });

        recyclerView.setAdapter(orderLineDeletedListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderLineViewModel.getAllCheckedOrderLines().observe(this, new Observer<List<OrderLine>>() {
            @Override
            public void onChanged(List<OrderLine> orderLines) {
                //update saved copy of lines
                orderLineDeletedListAdapter.setOrderLines(orderLines);
            }
        });

    }

    private void loadData() {
        orderLineDeletedListAdapter.notifyDataSetChanged();
    }
}
