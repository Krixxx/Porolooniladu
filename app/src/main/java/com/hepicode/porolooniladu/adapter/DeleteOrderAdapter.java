package com.hepicode.porolooniladu.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
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

import com.hepicode.porolooniladu.FoamInToDeBeletedActivity;
import com.hepicode.porolooniladu.R;
import com.hepicode.porolooniladu.model.OrderLine;
import com.hepicode.porolooniladu.model.OrderLineViewModel;
import com.hepicode.porolooniladu.model.OrderNumber;

import java.util.List;

public class DeleteOrderAdapter extends RecyclerView.Adapter<DeleteOrderAdapter.DeleteOrderViewHolder> {

    private Context context;
    private final LayoutInflater deleteOrderInflater;
    private List<OrderLine> orderLineList;
    private List<OrderNumber> orderNumberList;
    private OrderLineViewModel orderLineViewModel;
    private OrderNumber number;
    private int orderNum;
    private int checkedLinesCount;
    private int orderedLinesCount;
    private int orderNumb;

    public DeleteOrderAdapter(Context context) {
        this.context = context;
        deleteOrderInflater = LayoutInflater.from(context);

        orderLineViewModel = ViewModelProviders.of((FoamInToDeBeletedActivity) context).get(OrderLineViewModel.class);
    }


    @NonNull
    @Override
    public DeleteOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = deleteOrderInflater.inflate(R.layout.foam_in_to_be_deleted_order_row, parent, false);

        return new DeleteOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeleteOrderViewHolder holder, int position) {

        if (orderNumberList != null) {

            number = orderNumberList.get(position);
            orderNum = number.getOrderNumber();

            checkedLinesCount = checkedLinesCount(number, orderLineList);
            orderedLinesCount = orderedLinesCount(number, orderLineList);

            holder.orderNumber.setText(String.valueOf(orderNum));
            holder.checkedQuantity.setText(String.valueOf(checkedLinesCount));
            holder.orderedQuantity.setText(String.valueOf(orderedLinesCount));

            if (checkedLinesCount == orderedLinesCount && checkedLinesCount != 0){
                holder.isOk.setVisibility(View.VISIBLE);
            }else {
                holder.isOk.setVisibility(View.INVISIBLE);
            }

        } else {
            holder.orderNumber.setText(R.string.no_finished_orders);
        }
    }

    public void setOrderNumbers(List<OrderNumber> orderNumbers) {

        orderNumberList = orderNumbers;
        notifyDataSetChanged();
    }

    public void setOrderLines(List<OrderLine> orderLines) {

        orderLineList = orderLines;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {

        if (orderNumberList != null) {
            return orderNumberList.size();
        } else {
            return 0;
        }
    }

    public class DeleteOrderViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private TextView orderNumber, checkedQuantity, orderedQuantity;
        private ImageView isOk;

        public DeleteOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            orderNumber = itemView.findViewById(R.id.fi_tbt_order_number_textview);
            checkedQuantity = itemView.findViewById(R.id.fi_tbt_registered_quantity_textview);
            orderedQuantity = itemView.findViewById(R.id.fi_tbt_ordered_quantity_textview);
            isOk = itemView.findViewById(R.id.fi_tbt_checked_imageview);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            showAlertDialog();
            return false;
        }

        public void showAlertDialog() {

            orderNumb = orderNumberList.get(getAdapterPosition()).getOrderNumber();

            AlertDialog alert = new AlertDialog.Builder(context)
                    .setIcon(R.drawable.ic_delete_black)
                    .setTitle("Kustuta tellimus")
                    .setMessage("Kas oled kindel, et kustutada tellimus " + orderNumb + "?")
                    .setPositiveButton("JAH", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteFullOrder(orderNumberList.get(getAdapterPosition()), orderLineList);
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


    public void deleteFullOrder(OrderNumber orderNumber, List<OrderLine> itemsToDelete) {

        for (OrderLine line : itemsToDelete) {

            if (line.getOrderNumber() == orderNumber.getOrderNumber()) {
                orderLineViewModel.deleteAOrderLine(line);
            }
        }

        orderLineViewModel.deleteAOrderNumber(orderNumber);

        Toast.makeText(context, "Tellimus " + orderNumber.getOrderNumber() + " kustutatud!", Toast.LENGTH_SHORT).show();

    }

    private int checkedLinesCount(OrderNumber orderNumber, List<OrderLine> orderedLinesList){

        int num = 0;

        if (orderedLinesList != null){

            for (OrderLine line: orderedLinesList){

                if (line.getOrderNumber() == orderNumber.getOrderNumber() && line.getIsArrived() == 1){
                    num++;
                }
            }
        }
        return num;
    }

    private int orderedLinesCount(OrderNumber orderNumber, List<OrderLine> orderedLinesList){
        int num = 0;

        if (orderedLinesList != null){

            for (OrderLine line: orderedLinesList){

                if (line.getOrderNumber() == orderNumber.getOrderNumber()){
                    num++;
                }
            }
        }
        return num;
    }
}
