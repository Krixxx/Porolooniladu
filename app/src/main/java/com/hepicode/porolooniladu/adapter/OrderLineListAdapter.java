package com.hepicode.porolooniladu.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.hepicode.porolooniladu.FoamInActivity;
import com.hepicode.porolooniladu.FoamInDeletedActivity;
import com.hepicode.porolooniladu.MainActivity;
import com.hepicode.porolooniladu.R;
import com.hepicode.porolooniladu.model.OrderLine;
import com.hepicode.porolooniladu.model.OrderLineViewModel;
import com.hepicode.porolooniladu.util.FoamInBottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class OrderLineListAdapter extends RecyclerView.Adapter<OrderLineListAdapter.OrderLineViewHolder> implements Filterable {

    private Context context;
    private final LayoutInflater orderLineInflater;
    private List<OrderLine> orderLineList;
    private List<OrderLine> orderLineListFull;
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

        if (orderLineListFull != null) {

            OrderLine current = orderLineListFull.get(position);

            orderLineViewHolder.productCodeTextView.setText(current.getProductCode());
            orderLineViewHolder.orderedQuantityTextView.setText(String.valueOf(current.getOrderedQuantity()));

            orderLineViewHolder.okCheckBox.setOnCheckedChangeListener(null);
            orderLineViewHolder.okCheckBox.setChecked(false);

            if (current.getIsArrived() == 2 || current.getIsArrived() == 3) {

                orderLineViewHolder.singleLineLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.partiallyArrivedPosition));

            } else if (current.getIsArrived() == 0) {

                orderLineViewHolder.singleLineLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            }


        } else {
            orderLineViewHolder.productCodeTextView.setText(R.string.no_open_positions);
        }
    }

    public void setOrderLines(List<OrderLine> orderLines) {

        orderLineList = orderLines;
        orderLineListFull = new ArrayList<>(orderLines);

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (orderLineListFull != null) {
            return orderLineListFull.size();
        } else {
            return 0;
        }
    }

    public class OrderLineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView productCodeTextView, orderedQuantityTextView;
        public CheckBox okCheckBox;
        public ConstraintLayout singleLineLayout;

        public OrderLineViewHolder(@NonNull View itemView) {
            super(itemView);

            productCodeTextView = itemView.findViewById(R.id.foam_in_product_code);
            orderedQuantityTextView = itemView.findViewById(R.id.foam_in_quantity);
            okCheckBox = itemView.findViewById(R.id.foam_in_checkbox);
            singleLineLayout = itemView.findViewById(R.id.foam_in_single_line_layout);

            okCheckBox.setOnClickListener(this);
            productCodeTextView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();

            OrderLine line = orderLineListFull.get(position);

            switch (view.getId()) {

                case R.id.foam_in_checkbox:

                    if (okCheckBox.isChecked()) {

                        line.setIsArrived(1);
                        orderLineViewModel.update(line);
                        orderLineListFull.remove(position);
                        notifyDataSetChanged();

                    } else {
                        line.setIsArrived(0);
                        orderLineViewModel.update(line);
                    }

                    break;

                case R.id.foam_in_product_code:

                    pushFragment(context, line);

                    break;
            }
        }
    }

    @Override
    public Filter getFilter() {
        return orderFilter;
    }

    private Filter orderFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            String charString = charSequence.toString();
            if (charString.isEmpty()) {
                orderLineListFull = orderLineList;
            } else {
                List<OrderLine> filteredList = new ArrayList<>();
                for (OrderLine line : orderLineList) {
                    if (line.getProductCode().toLowerCase().contains(charString.toLowerCase())) {
                        filteredList.add(line);
                    }
                }
                orderLineListFull = filteredList;
            }

            FilterResults results = new FilterResults();
            results.values = orderLineListFull;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            orderLineListFull = (ArrayList<OrderLine>) filterResults.values;

            notifyDataSetChanged();
        }
    };

    public void pushFragment(Context context, OrderLine line) {
        FragmentManager fm = ((FoamInActivity) context).getSupportFragmentManager();
        FoamInBottomSheetDialog bottomSheet = new FoamInBottomSheetDialog();

        Bundle info = new Bundle();

        info.putInt("id", line.getId());
        info.putString("product_code", line.getProductCode());
        info.putInt("quantity", line.getOrderedQuantity());
        info.putInt("order_number", line.getOrderNumber());
        info.putInt("arrived_quantity", line.getArrivedQuantity());
        info.putInt("isArrived", line.getIsArrived());
        bottomSheet.setArguments(info);

        bottomSheet.show(fm, "");
    }
}
