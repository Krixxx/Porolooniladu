package com.hepicode.porolooniladu.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hepicode.porolooniladu.R;
import com.hepicode.porolooniladu.model.FabricsOutLine;
import com.hepicode.porolooniladu.model.FoamOutLine;
import com.hepicode.porolooniladu.model.OrderLineViewModel;

import java.text.NumberFormat;

public class FabricsOutBottomSheetDialog extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;

    private int id;
    private float outQuantity, partialQuantity;
    private String productCode, week, isGivenOut, isUrgent;
    private FabricsOutLine fabricsOutLine;
    private NumberFormat formatter = NumberFormat.getNumberInstance();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fabrics_out_bottom_sheet, container, false);

        TextView productCodeTxt = v.findViewById(R.id.bs_fabrics_out_product_code);
        TextView outQuantityTxt = v.findViewById(R.id.bs_fabrics_out_ordered_qty);
        TextView remainingQtyTxt = v.findViewById(R.id.bs_fabrics_out_remaining_qty);
        EditText givenQuantityEt = v.findViewById(R.id.bs_fabrics_out_actual_given_qty_et);
        Button saveButton = v.findViewById(R.id.bs_fabrics_out_save_btn);

        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        if (getArguments() != null) {
            id = getArguments().getInt("id");
            outQuantity = getArguments().getFloat("quantity");  //Ordered quantity
            partialQuantity = getArguments().getFloat("given_quantity");
            week = getArguments().getString("date");
            productCode = getArguments().getString("product_code");
            isUrgent = getArguments().getString("is_urgent");
            isGivenOut = getArguments().getString("is_filled");
        }

        String remainder = formatter.format(outQuantity-partialQuantity);
        productCodeTxt.setText(productCode);
        remainingQtyTxt.setText(remainder);
        outQuantityTxt.setText(Float.toString(outQuantity));

        fabricsOutLine = new FabricsOutLine(productCode, outQuantity, partialQuantity, week, isGivenOut, isUrgent);
        fabricsOutLine.setId(id);

        if (partialQuantity != 0) {
            givenQuantityEt.setText(Float.toString(partialQuantity));
        } else {
            partialQuantity = 0;
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!givenQuantityEt.getText().toString().isEmpty()) {
                    partialQuantity = Float.parseFloat(String.valueOf(givenQuantityEt.getText()));
                    fabricsOutLine.setGivenQuantity(partialQuantity);
                } else {
                    partialQuantity = 0;
                }

                mListener.onButtonClicked(id, isGivenOut, partialQuantity, week, fabricsOutLine);
                Log.d("BOTTOM_SHEET", "onClick: " + id + " " + week);
                dismiss();

            }
        });

        return v;
    }

    public interface BottomSheetListener {
        void onButtonClicked(int id, String isGivenOut, float partialQuantity, String weekNumber, FabricsOutLine line);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }

    }
}
