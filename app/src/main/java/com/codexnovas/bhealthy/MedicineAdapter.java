package com.codexnovas.bhealthy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {
    private List<MedicineDetails> medicineList;

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        public TextView medName, dosage, specificTime;
        public ImageView medImage;

        public MedicineViewHolder(View itemView) {
            super(itemView);
            medName = itemView.findViewById(R.id.med_name);
            dosage = itemView.findViewById(R.id.dosage);
            specificTime = itemView.findViewById(R.id.breakfast);
            medImage = itemView.findViewById(R.id.med1_img);
        }
    }

    public MedicineAdapter(List<MedicineDetails> medicineList) {
        this.medicineList = medicineList;
    }

    @Override
    public MedicineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.med_type2_card, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MedicineViewHolder holder, int position) {
        MedicineDetails currentMedicine = medicineList.get(position);
        holder.medName.setText(currentMedicine.getMedicineName());
        holder.dosage.setText(currentMedicine.getDosage());
        holder.specificTime.setText(currentMedicine.getSpecificTime());


    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }
}