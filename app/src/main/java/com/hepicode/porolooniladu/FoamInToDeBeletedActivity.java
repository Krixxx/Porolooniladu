package com.hepicode.porolooniladu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hepicode.porolooniladu.adapter.DeleteOrderAdapter;
import com.hepicode.porolooniladu.adapter.OrderLineListAdapter;
import com.hepicode.porolooniladu.model.OrderLine;
import com.hepicode.porolooniladu.model.OrderLineViewModel;
import com.hepicode.porolooniladu.model.OrderNumber;

import java.util.ArrayList;
import java.util.List;

public class FoamInToDeBeletedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderLineViewModel orderLineViewModel;
    private DividerItemDecoration dividerItemDecoration;
    private DeleteOrderAdapter deleteOrderAdapter;
    private FloatingActionButton infoFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foam_in_to_de_beleted);
        setTitle(R.string.title_forders_to_be_deleted);

        orderLineViewModel = ViewModelProviders.of(this).get(OrderLineViewModel.class);

        initViews();

        loadData();

        infoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int listSize = loadStatistics();

                Toast.makeText(FoamInToDeBeletedActivity.this, "Andmebaasis olevaid ridu: " + listSize, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initViews() {

        recyclerView = findViewById(R.id.fi_tbt_recyclerview);
        infoFab = findViewById(R.id.del_info_fab);

        deleteOrderAdapter = new DeleteOrderAdapter(this);

        recyclerView.setAdapter(deleteOrderAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void loadData(){

        orderLineViewModel.getAllOrderNumbers().observe(this, new Observer<List<OrderNumber>>() {
            @Override
            public void onChanged(List<OrderNumber> orderNumbers) {

                if (orderNumbers != null){

                    deleteOrderAdapter.setOrderNumbers(orderNumbers);

                }
            }
        });

        orderLineViewModel.getAllOrderLines().observe(this, new Observer<List<OrderLine>>() {
            @Override
            public void onChanged(List<OrderLine> orderLineList) {

                if (orderLineList != null){

                    deleteOrderAdapter.setOrderLines(orderLineList);
                }
            }
        });

        deleteOrderAdapter.notifyDataSetChanged();
    }

    private int loadStatistics() {

        final int[] quantity = new int[1];

        orderLineViewModel.getAllOrderLines().observe(this, new Observer<List<OrderLine>>() {
            @Override
            public void onChanged(List<OrderLine> orderLineList) {
                quantity[0] = orderLineList.size();
            }
        });

        return quantity[0];
    }
}
