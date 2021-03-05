package com.hepicode.porolooniladu.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hepicode.porolooniladu.FabricsOutActivity;
import com.hepicode.porolooniladu.FoamOutActivity;
import com.hepicode.porolooniladu.R;
import com.hepicode.porolooniladu.model.FabricsOutLine;
import com.hepicode.porolooniladu.model.FoamOutLine;
import com.hepicode.porolooniladu.util.FabricsOutBottomSheetDialog;
import com.hepicode.porolooniladu.util.FoamOutBottomSheetDialog;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FabricsOutListAdapter extends RecyclerView.Adapter<FabricsOutListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FabricsOutLine> lineList;
    private OnCheckBoxClickListener mOnCheckBoxClickListener;


    public FabricsOutListAdapter(Context context, ArrayList<FabricsOutLine> lineList, OnCheckBoxClickListener onCheckBoxClickListener) {
        this.context = context;
        this.lineList = lineList;
        this.mOnCheckBoxClickListener = onCheckBoxClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fabrics_out_single_line, parent, false);

        return new ViewHolder(view, mOnCheckBoxClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final FabricsOutListAdapter.ViewHolder holder, final int position) {

        if (lineList != null) {

            FabricsOutLine line = lineList.get(position);

            holder.lineDate.setText(line.getDate());
            holder.lineProductCode.setText(line.getProductCode());
            holder.lineQuantity.setText(Float.toString(line.getQuantity()));

            if (line.getIsUrgent().equals("TRUE")) {
                holder.lineLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.urgentFabricLine));
            } else if (line.getGivenQuantity() > 0 && line.getGivenQuantity() < line.getQuantity()) {
                holder.lineLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.previousDatePosition));
            } else {
                holder.lineLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            }

            if (line.getIsFilled().equals("FALSE")) {
                holder.isFilled.setChecked(false);
            } else if (line.getIsFilled().equals("TRUE")) {
                holder.isFilled.setChecked(true);
            }

        }

    }

    @Override
    public int getItemCount() {
        return lineList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView lineDate, lineProductCode, lineQuantity;
        private CheckBox isFilled;
        private ConstraintLayout lineLayout;
        private OnCheckBoxClickListener onCheckBoxClickListener;

        public ViewHolder(@NonNull View itemView, OnCheckBoxClickListener onCheckBoxClickListener) {
            super(itemView);

            lineDate = itemView.findViewById(R.id.fabrics_out_week);
            lineProductCode = itemView.findViewById(R.id.fabrics_out_product_code);
            lineQuantity = itemView.findViewById(R.id.fabrics_out_quantity);
            isFilled = itemView.findViewById(R.id.fabrics_out_checkbox);
            lineLayout = itemView.findViewById(R.id.fabrics_out_single_line_layout);
            this.onCheckBoxClickListener = onCheckBoxClickListener;

            isFilled.setOnClickListener(this);
            lineProductCode.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.fabrics_out_checkbox:
                    onCheckBoxClickListener.onCheckBoxClick(getAdapterPosition());
                    break;

                case R.id.fabrics_out_product_code:
                    pushFragment(context, lineList.get(getAdapterPosition()));
                    break;
            }

        }
    }

    public interface OnCheckBoxClickListener {
        void onCheckBoxClick(int position);
    }

    public void pushFragment(Context context, FabricsOutLine line) {
        FragmentManager fm = ((FabricsOutActivity) context).getSupportFragmentManager();
        FabricsOutBottomSheetDialog bottomSheet = new FabricsOutBottomSheetDialog();

        Bundle info = new Bundle();

        info.putInt("id", line.getId());
        info.putString("product_code", line.getProductCode());
        info.putFloat("quantity", line.getQuantity());
        info.putFloat("given_quantity", line.getGivenQuantity());
        info.putString("date", line.getDate());
        info.putString("is_filled", line.getIsFilled());
        info.putString("is_urgent", line.getIsUrgent());
        bottomSheet.setArguments(info);

        bottomSheet.show(fm, "");
    }
}
