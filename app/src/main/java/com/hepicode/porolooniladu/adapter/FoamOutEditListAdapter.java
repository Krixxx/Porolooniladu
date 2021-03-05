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

import com.hepicode.porolooniladu.FoamOutEditActivity;
import com.hepicode.porolooniladu.R;
import com.hepicode.porolooniladu.model.FoamOutLine;
import com.hepicode.porolooniladu.model.OrderLineViewModel;

import java.util.List;

public class FoamOutEditListAdapter extends RecyclerView.Adapter<FoamOutEditListAdapter.FoamOutEditViewHolder> {

    private Context context;
    private final LayoutInflater foamOutEditInflater;
    private List<FoamOutLine> foamOutLineList;
    private OrderLineViewModel orderLineViewModel;

    public FoamOutEditListAdapter(Context context) {
        this.context = context;
        foamOutEditInflater = LayoutInflater.from(context);

        orderLineViewModel = ViewModelProviders.of((FoamOutEditActivity) context).get(OrderLineViewModel.class);
    }

    @NonNull
    @Override
    public FoamOutEditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = foamOutEditInflater.inflate(R.layout.foam_out_single_line, parent, false);

        return new FoamOutEditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoamOutEditViewHolder holder, int position) {

        if (foamOutLineList != null) {

            FoamOutLine current = foamOutLineList.get(position);

            holder.dateText.setText(current.getDateOut());
            holder.productCodeText.setText(current.getProductCode());
            holder.quantityText.setText(String.valueOf(current.getOutQuantity()));
            holder.checkBox.setChecked(true);

        }
    }

    public void setFoamOutEditLines(List<FoamOutLine> foamOutLines) {
        foamOutLineList = foamOutLines;

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (foamOutLineList != null) {
            return foamOutLineList.size();
        } else {
            return 0;
        }
    }

    public class FoamOutEditViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView dateText, productCodeText, quantityText;
        private CheckBox checkBox;

        public FoamOutEditViewHolder(@NonNull View itemView) {
            super(itemView);

            dateText = itemView.findViewById(R.id.foam_out_date);
            productCodeText = itemView.findViewById(R.id.foam_out_product_code);
            quantityText = itemView.findViewById(R.id.foam_out_quantity);
            checkBox = itemView.findViewById(R.id.foam_out_checkbox);

            checkBox.setOnClickListener(this);
            productCodeText.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {

                FoamOutLine line = foamOutLineList.get(position);

                switch (view.getId()) {

                    case R.id.foam_out_checkbox:

                        if (checkBox.isChecked()) {

                            line.setIsGivenOut(1);
                            orderLineViewModel.updateFoamOutLine(line);

                        } else {
                            line.setIsGivenOut(0);
                            orderLineViewModel.updateFoamOutLine(line);
                            foamOutLineList.remove(position);
                        }

                        break;

                    case R.id.foam_out_product_code:
                        Toast.makeText(context, "Status: " + line.getIsGivenOut(), Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        }
    }
}
