package com.hepicode.porolooniladu.adapter;

import android.content.Context;
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

public class OrderLineDeletedListAdapter extends RecyclerView.Adapter<OrderLineDeletedListAdapter.OrderLineViewHolder> {

    private Context context;
    private final LayoutInflater orderLineInflater;
    private List<OrderLine> orderLineList;
    private OrderLineViewModel orderLineViewModel;

    public OrderLineDeletedListAdapter(Context context) {
        this.context = context;
        orderLineInflater = LayoutInflater.from(context);

        orderLineViewModel = ViewModelProviders.of((FoamInDeletedActivity) context).get(OrderLineViewModel.class);
    }

    @NonNull
    @Override
    public OrderLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = orderLineInflater.inflate(R.layout.foam_in_deleted_single_line, parent, false);

        return new OrderLineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderLineViewHolder orderLineViewHolder, int position) {

        if (orderLineList != null) {

            OrderLine current = orderLineList.get(position);

            orderLineViewHolder.orderNumberTextView.setText(String.valueOf(current.getOrderNumber()));
            orderLineViewHolder.productCodeTextView.setText(current.getProductCode());
            orderLineViewHolder.orderedQuantityTextView.setText(String.valueOf(current.getOrderedQuantity()));
            orderLineViewHolder.okCheckBox.setChecked(true);

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

        public TextView productCodeTextView, orderedQuantityTextView, orderNumberTextView;
        public CheckBox okCheckBox;

        public OrderLineViewHolder(@NonNull View itemView) {
            super(itemView);

            orderNumberTextView = itemView.findViewById(R.id.foam_in_order_number_del);
            productCodeTextView = itemView.findViewById(R.id.foam_in_product_code_del);
            orderedQuantityTextView = itemView.findViewById(R.id.foam_in_quantity_del);
            okCheckBox = itemView.findViewById(R.id.foam_in_checkbox_del);

            okCheckBox.setOnClickListener(this);
            productCodeTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {

                OrderLine line = orderLineList.get(position);

                switch (view.getId()) {

                    case R.id.foam_in_checkbox_del:

                        if (okCheckBox.isChecked()) {

                            line.setIsArrived(1);
                            orderLineViewModel.update(line);

                        } else {
                            line.setIsArrived(0);
                            orderLineViewModel.update(line);
                            orderLineList.remove(getAdapterPosition());
                        }

                        break;

                    case R.id.foam_in_product_code_del:
                        Toast.makeText(context, "Status: " + line.getIsArrived(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

        }

    }
}
