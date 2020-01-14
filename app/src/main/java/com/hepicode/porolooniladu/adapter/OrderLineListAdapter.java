package com.hepicode.porolooniladu.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.hepicode.porolooniladu.FoamInActivity;
import com.hepicode.porolooniladu.FoamInDeletedActivity;
import com.hepicode.porolooniladu.R;
import com.hepicode.porolooniladu.model.OrderLine;
import com.hepicode.porolooniladu.model.OrderLineViewModel;

import java.util.List;
import java.util.zip.Inflater;

public class OrderLineListAdapter extends RecyclerView.Adapter<OrderLineListAdapter.OrderLineViewHolder> {

    private Context context;
    private final LayoutInflater orderLineInflater;
    private List<OrderLine> orderLineList;
    private OrderLineViewModel orderLineViewModel;

    public OrderLineListAdapter(Context context) {
        this.context = context;
        orderLineInflater = LayoutInflater.from(context);

        orderLineViewModel = ViewModelProviders.of((FoamInActivity) context).get(OrderLineViewModel.class);

    }

    @NonNull
    @Override
    public OrderLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = orderLineInflater.inflate(R.layout.foam_in_single_line, parent, false);

        return new OrderLineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderLineViewHolder orderLineViewHolder, int position) {

        if (orderLineList != null) {

            OrderLine current = orderLineList.get(position);

            orderLineViewHolder.productCodeTextView.setText(current.getProductCode());
            orderLineViewHolder.orderedQuantityTextView.setText(String.valueOf(current.getOrderedQuantity()));

            orderLineViewHolder.okCheckBox.setOnCheckedChangeListener(null);
            orderLineViewHolder.okCheckBox.setChecked(false);

        } else {
            orderLineViewHolder.productCodeTextView.setText(R.string.no_open_positions);
        }
    }

    public void setOrderLines(List<OrderLine> orderLines) {

        orderLineList = orderLines;

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (orderLineList != null) {
            return orderLineList.size();
        } else {
            return 0;
        }
    }

    public class OrderLineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView productCodeTextView, orderedQuantityTextView;
        public CheckBox okCheckBox;

        public OrderLineViewHolder(@NonNull View itemView) {
            super(itemView);

            productCodeTextView = itemView.findViewById(R.id.foam_in_product_code);
            orderedQuantityTextView = itemView.findViewById(R.id.foam_in_quantity);
            okCheckBox = itemView.findViewById(R.id.foam_in_checkbox);

            okCheckBox.setOnClickListener(this);
            productCodeTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();

            OrderLine line = orderLineList.get(position);

            switch (view.getId()) {

                case R.id.foam_in_checkbox:

                    if (okCheckBox.isChecked()) {

                        line.setIsArrived(1);
                        orderLineViewModel.update(line);
                        orderLineList.remove(position);
                        notifyItemRemoved(position);

                    } else {
                        line.setIsArrived(0);
                        orderLineViewModel.update(line);
                    }

                    break;

                case R.id.foam_in_product_code:
                    Toast.makeText(context, "Status: " + line.getIsArrived(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
