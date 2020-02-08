package com.hepicode.porolooniladu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;

import com.hepicode.porolooniladu.adapter.FoamOutEditListAdapter;
import com.hepicode.porolooniladu.model.FoamOutLine;
import com.hepicode.porolooniladu.model.OrderLineViewModel;

import java.util.List;

public class FoamOutEditActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FoamOutEditListAdapter foamOutEditListAdapter;
    private OrderLineViewModel orderLineViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foam_out_edit);

        orderLineViewModel = ViewModelProviders.of(this).get(OrderLineViewModel.class);

        initViews();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                loadData();
            }
        });

        loadData();
    }

    private void initViews() {

        swipeRefreshLayout = findViewById(R.id.refresh_layout_edit);

        recyclerView = findViewById(R.id.foam_out_recyclerview_edit);

        foamOutEditListAdapter = new FoamOutEditListAdapter(this);

        recyclerView.setAdapter(foamOutEditListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadData() {

        orderLineViewModel.getAllCheckedFoamOutLines().observe(this, new Observer<List<FoamOutLine>>() {
            @Override
            public void onChanged(List<FoamOutLine> foamOutLines) {
                foamOutEditListAdapter.setFoamOutEditLines(foamOutLines);
            }
        });
    }
}
