package com.hepicode.porolooniladu.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.hepicode.porolooniladu.FoamOutActivity;
import com.hepicode.porolooniladu.R;
import com.hepicode.porolooniladu.model.FoamOutLine;
import com.hepicode.porolooniladu.model.OrderLineViewModel;
import com.hepicode.porolooniladu.util.CalendarModel;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FoamOutListAdapter extends RecyclerView.Adapter<FoamOutListAdapter.FoamOutLineViewHolder> {

    private Context context;
    private final LayoutInflater foamLineInflater;
    private OrderLineViewModel orderLineViewModel;
    private List<FoamOutLine> foamOutLineList;

    private Calendar todayCal = Calendar.getInstance();
    private Date toDay = todayCal.getTime();

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat estSdf = new SimpleDateFormat("dd.MM.yyyy");
    String strDate = sdf.format(toDay);
    private Date lineDate = new Date(), parsedDate = new Date();
    private FoamOutLine current;

    public FoamOutListAdapter(Context context) {
        this.context = context;
        this.foamLineInflater = LayoutInflater.from(context);
        this.orderLineViewModel = ViewModelProviders.of((FoamOutActivity) context).get(OrderLineViewModel.class);
    }

    @NonNull
    @Override
    public FoamOutLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = foamLineInflater.inflate(R.layout.foam_out_single_line, parent, false);

        return new FoamOutLineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoamOutLineViewHolder holder, int position) {

        if (foamOutLineList != null){

            current = foamOutLineList.get(position);

            try {
                parsedDate = sdf.parse(strDate);
                lineDate = sdf.parse(current.getDateOut());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d("ADAPTER", "onBindViewHolder parsed: " + parsedDate);
            Log.d("ADAPTER", "onBindViewHolder line: " + lineDate);


            holder.dateTextView.setText(current.getDateOut());
            holder.productCodeTextView.setText(current.getProductCode());
            holder.quantityTextView.setText(String.valueOf(current.getOutQuantity()));

            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(false);

            if (lineDate != null && lineDate.before(parsedDate)) {
                holder.singleLineLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.partiallyArrivedPosition));
            } else if (lineDate.equals(parsedDate)){
                holder.singleLineLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.onDatePosition));
            } else {
                holder.singleLineLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            }
        }
    }

    public void setFoamOutLinesList(List<FoamOutLine> foamOutLines){

        foamOutLineList = foamOutLines;

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (foamOutLineList != null){
            return foamOutLineList.size();
        } else {
            return 0;
        }
    }

    public class FoamOutLineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView dateTextView, productCodeTextView, quantityTextView;
        private CheckBox checkBox;
        private ConstraintLayout singleLineLayout;

        public FoamOutLineViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTextView = itemView.findViewById(R.id.foam_out_date);
            productCodeTextView = itemView.findViewById(R.id.foam_out_product_code);
            quantityTextView = itemView.findViewById(R.id.foam_out_quantity);
            checkBox = itemView.findViewById(R.id.foam_out_checkbox);
            singleLineLayout = itemView.findViewById(R.id.foam_out_single_line_layout);

            checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();

            FoamOutLine foamOutLine = foamOutLineList.get(position);

            switch (view.getId()){

                case R.id.foam_out_checkbox:

                    if (checkBox.isChecked()){
                        foamOutLine.setIsGivenOut(1);
                        orderLineViewModel.updateFoamOutLine(foamOutLine);
                        foamOutLineList.remove(position);
                        notifyDataSetChanged();
                    } else {
                        foamOutLine.setIsGivenOut(0);
                        orderLineViewModel.updateFoamOutLine(foamOutLine);
                    }
                    break;
            }
        }
    }
}
