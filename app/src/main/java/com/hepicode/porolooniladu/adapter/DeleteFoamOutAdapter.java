package com.hepicode.porolooniladu.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.hepicode.porolooniladu.FoamOutToBeDeletedActivity;
import com.hepicode.porolooniladu.R;
import com.hepicode.porolooniladu.model.FoamOutLine;
import com.hepicode.porolooniladu.model.FoamOutWeekNumber;
import com.hepicode.porolooniladu.model.OrderLineViewModel;

import java.util.List;

public class DeleteFoamOutAdapter extends RecyclerView.Adapter<DeleteFoamOutAdapter.DeleteFoamOutViewHolder> {

    private Context context;
    private final LayoutInflater foamOutLineInflater;
    private List<FoamOutLine> foamOutLineList;
    private List<FoamOutWeekNumber> foamOutWeekNumberList;
    private OrderLineViewModel orderLineViewModel;

    private FoamOutWeekNumber weekNumber;

    private String weekNum;
    private int checkedLineCount;
    private int plannedLineCount;
    private String planNum;


    public DeleteFoamOutAdapter(Context context) {
        this.context = context;
        foamOutLineInflater = LayoutInflater.from(context);

        orderLineViewModel = ViewModelProviders.of((FoamOutToBeDeletedActivity) context).get(OrderLineViewModel.class);
    }

    @NonNull
    @Override
    public DeleteFoamOutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = foamOutLineInflater.inflate(R.layout.foam_out_to_be_deleted_order_row, parent, false);

        return new DeleteFoamOutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeleteFoamOutViewHolder holder, int position) {

        if (foamOutWeekNumberList != null){

            weekNumber = foamOutWeekNumberList.get(position);
            weekNum = weekNumber.getWeekNumber();

            checkedLineCount = checkedLinesCount(weekNumber, foamOutLineList);
            plannedLineCount = plannedLinesCount(weekNumber, foamOutLineList);

            holder.weekNumber.setText(weekNum);
            holder.checkedQuantity.setText(String.valueOf(checkedLineCount));
            holder.plannedQuantity.setText(String.valueOf(plannedLineCount));

            if (checkedLineCount == plannedLineCount && checkedLineCount != 0){
                holder.isOkImageView.setVisibility(View.VISIBLE);
            }else {
                holder.isOkImageView.setVisibility(View.INVISIBLE);
            }

        }

    }

    public void setWeekNumbers(List<FoamOutWeekNumber> weekNumbers) {

        foamOutWeekNumberList = weekNumbers;
        notifyDataSetChanged();
    }

    public void setWeekLines(List<FoamOutLine> weekLines) {

        foamOutLineList = weekLines;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (foamOutWeekNumberList != null){
            return foamOutWeekNumberList.size();
        } else {
            return 0;
        }
    }

    public class DeleteFoamOutViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private TextView weekNumber, checkedQuantity, plannedQuantity;
        private ImageView isOkImageView;


        public DeleteFoamOutViewHolder(@NonNull View itemView) {
            super(itemView);

            weekNumber = itemView.findViewById(R.id.fo_tbt_week_number_textview);
            checkedQuantity = itemView.findViewById(R.id.fo_tbt_registered_quantity_textview);
            plannedQuantity = itemView.findViewById(R.id.fo_tbt_ordered_quantity_textview);
            isOkImageView = itemView.findViewById(R.id.fo_tbt_checked_imageview);

            itemView.setOnLongClickListener(this);

        }

        @Override
        public boolean onLongClick(View view) {

            showAlertDialog();
            return false;
        }

        public void showAlertDialog() {

            planNum = foamOutWeekNumberList.get(getAdapterPosition()).getWeekNumber();

            AlertDialog alert = new AlertDialog.Builder(context)
                    .setIcon(R.drawable.ic_delete_black)
                    .setTitle("Kustuta nädala plaan")
                    .setMessage("Kas oled kindel, et kustutada " + planNum + " plaan?")
                    .setPositiveButton("JAH", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteFullOrder(foamOutWeekNumberList.get(getAdapterPosition()), foamOutLineList);
                        }
                    })
                    .setNegativeButton("EI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }
    }

    public void deleteFullOrder(FoamOutWeekNumber weekNumber, List<FoamOutLine> itemsToDelete) {

        for (FoamOutLine line : itemsToDelete) {

            if (line.getWeekNumber().equals(weekNumber.getWeekNumber())) {
                orderLineViewModel.deleteFoamOutWeek(line.getWeekNumber());
            }
        }

        orderLineViewModel.deleteFoamOutWeekNr(weekNumber);

        Toast.makeText(context, weekNumber.getWeekNumber() + " plaan kustutatud!", Toast.LENGTH_SHORT).show();

    }

    private int checkedLinesCount(FoamOutWeekNumber weekNumber, List<FoamOutLine> foamOutLinesList){

        int num = 0;

        if (foamOutLinesList != null){

            for (FoamOutLine line: foamOutLinesList){

                if (line.getWeekNumber().equals(weekNumber.getWeekNumber()) && line.getIsGivenOut() == 1){
                    num++;
                }
            }
        }
        return num;
    }

    private int plannedLinesCount(FoamOutWeekNumber weekNumber, List<FoamOutLine> foamOutLinesList){
        int num = 0;

        if (foamOutLinesList != null){

            for (FoamOutLine line: foamOutLinesList){

                if (line.getWeekNumber().equals(weekNumber.getWeekNumber())){
                    num++;
                }
            }
        }
        return num;
    }
}
