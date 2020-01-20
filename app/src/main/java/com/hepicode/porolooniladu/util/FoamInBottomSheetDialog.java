package com.hepicode.porolooniladu.util;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hepicode.porolooniladu.R;
import com.hepicode.porolooniladu.model.OrderLine;
import com.hepicode.porolooniladu.model.OrderLineViewModel;

public class FoamInBottomSheetDialog extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;
    private OrderLineViewModel mModel;

    private int id, quantity, orderNumber, arrivedQuantityInt, isArrivedInt, isArrived, arrivedQty;
    private String productCodeTxt;
    private OrderLine orderLine;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.foam_in_bottom_sheet, container, false);

        mModel = ViewModelProviders.of(this).get(OrderLineViewModel.class);

        TextView productCode = v.findViewById(R.id.bs_foam_in_product_code);
        TextView orderedQuantity = v.findViewById(R.id.bs_foam_in_ordered_qty);
        CheckBox detailsMissing = v.findViewById(R.id.bs_foam_in_details_missing);
        CheckBox setsMissing = v.findViewById(R.id.bs_foam_in_sets_missing);
        CheckBox allArrived = v.findViewById(R.id.bs_foam_in_all_arrived);
        EditText arrivedQuantity = v.findViewById(R.id.bs_arrived_qty_et);
        Button saveButton = v.findViewById(R.id.bs_save_btn);

        if (getArguments() != null){
            id = getArguments().getInt("id");
            quantity = getArguments().getInt("quantity");  //Ordered quantity
            orderNumber = getArguments().getInt("order_number");
            productCodeTxt = getArguments().getString("product_code");
            arrivedQuantityInt = getArguments().getInt("arrived_quantity");
            isArrivedInt = getArguments().getInt("isArrived");
        }

        productCode.setText(productCodeTxt);
        orderedQuantity.setText(Integer.toString(quantity));

        orderLine = new OrderLine(productCodeTxt, quantity, arrivedQuantityInt, isArrivedInt, orderNumber);
        orderLine.setId(id);

        if (arrivedQuantityInt != 0){
            arrivedQuantity.setText(Integer.toString(arrivedQuantityInt));
        } else{
            arrivedQty = 0;
        }



        if (isArrivedInt != 0){
            if (isArrivedInt == 2){
                detailsMissing.setChecked(true);
            }else if (isArrivedInt == 3){
                setsMissing.setChecked(true);
            }
        }else {
            isArrived = 0;
        }



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!arrivedQuantity.getText().toString().isEmpty()){
                    arrivedQty = Integer.parseInt(String.valueOf(arrivedQuantity.getText()));
                }else {
                    arrivedQty = 0;
                }

                if (detailsMissing.isChecked()){
                    isArrived = 2;
                }else if (setsMissing.isChecked()){
                    isArrived = 3;
                }else if (allArrived.isChecked()){
                    isArrived = 1;
                    arrivedQty = quantity;
                }

                mListener.onButtonClicked(id, isArrived, arrivedQty, orderNumber, orderLine);
                Log.d("BOTTOM_SHEET", "onClick: " + id + " " + orderNumber);
                dismiss();

            }
        });

        return v;
    }

    public interface BottomSheetListener{
        void onButtonClicked(int id, int isArrived, int quantity, int orderNumber, OrderLine line);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
            + " must implement BottomSheetListener");
        }

    }
}
