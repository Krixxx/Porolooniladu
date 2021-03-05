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
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hepicode.porolooniladu.FoamOutActivity;
import com.hepicode.porolooniladu.R;
import com.hepicode.porolooniladu.model.FoamOutLine;
import com.hepicode.porolooniladu.model.OrderLine;
import com.hepicode.porolooniladu.model.OrderLineViewModel;

public class FoamOutBottomSheetDialog extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;
    private OrderLineViewModel mModel;

    private int id, outQuantity, partialQuantity, isGivenOut;
    private String productCode,date, weekNumber;
    private FoamOutLine foamOutLine;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.foam_out_bottom_sheet, container, false);

        mModel = ViewModelProviders.of(this).get(OrderLineViewModel.class);

        TextView productCodeTxt = v.findViewById(R.id.bs_foam_out_product_code);
        TextView outQuantityTxt = v.findViewById(R.id.bs_foam_out_ordered_qty);
        EditText givenQuantityEt = v.findViewById(R.id.bs_foam_out_actual_given_qty_et);
        Button saveButton = v.findViewById(R.id.bs_foam_out_save_btn);

        if (getArguments() != null) {
            id = getArguments().getInt("id");
            outQuantity = getArguments().getInt("quantity");  //Ordered quantity
            weekNumber = getArguments().getString("week_number");
            date = getArguments().getString("date");
            productCode = getArguments().getString("product_code");
            partialQuantity = getArguments().getInt("partial_quantity");
            isGivenOut = getArguments().getInt("is_given_out");
        }

        productCodeTxt.setText(productCode);
        outQuantityTxt.setText(Integer.toString(outQuantity));

        foamOutLine = new FoamOutLine(productCode, outQuantity, partialQuantity, date, isGivenOut, weekNumber);
        foamOutLine.setId(id);

        if (partialQuantity != 0) {
            givenQuantityEt.setText(Integer.toString(partialQuantity));
        } else {
            partialQuantity = 0;
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!givenQuantityEt.getText().toString().isEmpty()){
                    partialQuantity = Integer.parseInt(String.valueOf(givenQuantityEt.getText()));
                } else {
                    partialQuantity = 0;
                }

                foamOutLine.setPartialQuantity(partialQuantity);


//                if (!givenQuantityEt.getText().toString().isEmpty() && Integer.parseInt(String.valueOf(givenQuantityEt.getText())) < outQuantity) {
//                    partialQuantity = Integer.parseInt(String.valueOf(givenQuantityEt.getText()));
//                    foamOutLine.setIsGivenOut(2);
//                    isGivenOut = 2;
//                } else if (Integer.parseInt(String.valueOf(givenQuantityEt.getText())) == outQuantity){
//                    foamOutLine.setIsGivenOut(1);
//                    isGivenOut = 1;
//                } else if (Integer.parseInt(String.valueOf(givenQuantityEt.getText())) > outQuantity){
//                    Toast.makeText(getContext(), "Kogus ei saa olla suurem kui plaanitud!", Toast.LENGTH_SHORT).show();
//                    foamOutLine.setPartialQuantity(0);
//                    partialQuantity = 0;
//                } else {
//                    foamOutLine.setPartialQuantity(0);
//                    partialQuantity = 0;
//                }

                mListener.onButtonClicked(id, isGivenOut, partialQuantity, weekNumber, foamOutLine);
                Log.d("BOTTOM_SHEET", "onClick: " + id + " " + weekNumber);
                dismiss();

            }
        });

        return v;
    }

    public interface BottomSheetListener {
        void onButtonClicked(int id, int isGivenOut, int partialQuantity, String weekNumber, FoamOutLine line);
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
