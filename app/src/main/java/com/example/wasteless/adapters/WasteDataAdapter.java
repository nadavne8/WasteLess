package com.example.wasteless.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wasteless.R;
import com.example.wasteless.models.UserWasteCollection;
import com.example.wasteless.models.WasteDataModel;
import com.example.wasteless.utils.GenericUtils;

import java.util.List;

public class WasteDataAdapter extends RecyclerView.Adapter<WasteDataAdapter.ViewHolder> {

    private final List<WasteDataModel> wasteDataList;

    public WasteDataAdapter(List<WasteDataModel> wasteDataList) {
        this.wasteDataList = wasteDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_waste_data, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WasteDataModel wasteData = wasteDataList.get(position);
        holder.textViewProductName.setText(wasteData.getProductName());
        holder.textViewWeight.setText("Weight: " + wasteData.getWeight() + " kg");

        if (wasteData.getProductImage() != null && !wasteData.getProductImage().isEmpty()) {
            GenericUtils.base64StringToImageView(wasteData.getProductImage(), holder.productImage);
            holder.productImage.setVisibility(View.VISIBLE);
        }

        holder.deleteButton.setOnClickListener(view -> {
            // TODO: Get the date from the collection
            UserWasteCollection.getInstance(null).deleteWasteData(GenericUtils.getCurrentWeekSundayDateKey(), wasteData.getUuid());
        });
    }

    @Override
    public int getItemCount() {
        return wasteDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewProductName;
        TextView textViewWeight;
        ImageView productImage;
        Button deleteButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewWeight = itemView.findViewById(R.id.textViewWeight);
            productImage = itemView.findViewById(R.id.productImage);
            deleteButton = itemView.findViewById(R.id.deleteButton);

        }
    }
}


