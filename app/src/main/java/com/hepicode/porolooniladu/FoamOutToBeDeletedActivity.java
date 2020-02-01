package com.hepicode.porolooniladu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hepicode.porolooniladu.adapter.DeleteFoamOutAdapter;
import com.hepicode.porolooniladu.adapter.DeleteOrderAdapter;
import com.hepicode.porolooniladu.model.FoamOutLine;
import com.hepicode.porolooniladu.model.FoamOutWeekNumber;
import com.hepicode.porolooniladu.model.OrderLineViewModel;

import java.security.PrivateKey;
import java.util.List;

public class FoamOutToBeDeletedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderLineViewModel orderLineViewModel;
    private DividerItemDecoration dividerItemDecoration;
    private DeleteFoamOutAdapter deleteFoamOutAdapter;
    private FloatingActionButton delFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foam_out_to_be_deleted);
        setTitle(R.string.foam_out_to_be_deleted_title);

        orderLineViewModel = ViewModelProviders.of(this).get(OrderLineViewModel.class);

        initViews();

        loadData();

        delFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int listSize = loadStatistics();

                Toast.makeText(FoamOutToBeDeletedActivity.this, "Andmebaasis olevaid ridu: " + listSize, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void initViews(){

        recyclerView = findViewById(R.id.fo_tbd_recyclerview);
        delFab = findViewById(R.id.foam_out_del_info_fab);

        deleteFoamOutAdapter = new DeleteFoamOutAdapter(this);

        recyclerView.setAdapter(deleteFoamOutAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

    }

    public void loadData(){

        orderLineViewModel.getAllWeekNumbers().observe(this, new Observer<List<FoamOutWeekNumber>>() {
            @Override
            public void onChanged(List<FoamOutWeekNumber> foamOutWeekNumbers) {

                if (foamOutWeekNumbers != null){

                    deleteFoamOutAdapter.setWeekNumbers(foamOutWeekNumbers);

                }
            }
        });

        orderLineViewModel.getAllFoamLines().observe(this, new Observer<List<FoamOutLine>>() {
            @Override
            public void onChanged(List<FoamOutLine> foamOutLines) {

                if (foamOutLines != null){

                    deleteFoamOutAdapter.setWeekLines(foamOutLines);

                }

            }
        });

        deleteFoamOutAdapter.notifyDataSetChanged();
    }

    private int loadStatistics(){

        final int[] quantity = new int[1];

        orderLineViewModel.getAllFoamLines().observe(this, new Observer<List<FoamOutLine>>() {
            @Override
            public void onChanged(List<FoamOutLine> foamOutLines) {
                quantity[0] = foamOutLines.size();
            }
        });

        return quantity[0];

    }
}
