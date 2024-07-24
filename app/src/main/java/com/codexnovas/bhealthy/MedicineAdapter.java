package com.codexnovas.bhealthy;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {
    private List<MedicineDetails> medicineList;
    private Context context;
    private DatabaseReference databaseReference; // Add this line

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        public TextView medName, dosage, specificTime, done;
        public ImageView medImage;

        public MedicineViewHolder(View itemView) {
            super(itemView);
            medName = itemView.findViewById(R.id.med_name);
            dosage = itemView.findViewById(R.id.dosage);
            specificTime = itemView.findViewById(R.id.breakfast);
            medImage = itemView.findViewById(R.id.med2_img);
            done = itemView.findViewById(R.id.done);
        }
    }

    public MedicineAdapter(Context context, List<MedicineDetails> medicineList) {
        this.context = context;
        this.medicineList = medicineList;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("medicineDetails");
        }
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

        holder.done.setOnClickListener(v -> {
            showMedicineOptionsDialog(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    private void showMedicineOptionsDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_medicine_options, null);
        builder.setView(dialogView);

        RadioGroup radioGroupOptions = dialogView.findViewById(R.id.radioGroupOptions);
        RadioButton radioTakeToday = dialogView.findViewById(R.id.radioTakeToday);
        RadioButton radioCompleteCourse = dialogView.findViewById(R.id.radioCompleteCourse);
        RadioButton radioDeleteReminder = dialogView.findViewById(R.id.radioDeleteReminder);

        builder.setPositiveButton("OK", (dialog, which) -> {
            int selectedOptionId = radioGroupOptions.getCheckedRadioButtonId();
            if (selectedOptionId == radioTakeToday.getId()) {
                // Handle taking today's dosage
            } else if (selectedOptionId == radioCompleteCourse.getId()) {
                // Handle completing the medicine course
            } else if (selectedOptionId == radioDeleteReminder.getId()) {
                // Handle deleting the medicine reminder
                deleteMedicineDetail(position);
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteMedicineDetail(int position) {
        Log.d("DeleteMedicineDetail", "Attempting to delete item at position: " + position);
        if (position < 0 || position >= medicineList.size()) {
            Log.e("DeleteMedicineDetail", "Invalid position: " + position);
            return; // Exit if the position is out of bounds
        }

        MedicineDetails medicine = medicineList.get(position);
        String key = medicine.getKey();
        databaseReference.child(key).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (position < medicineList.size()) {
                    Log.d("DeleteMedicineDetail", "Removing item at position: " + position);
                    medicineList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, medicineList.size());
                    Toast.makeText(context, "Medicine detail deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("DeleteMedicineDetail", "Position out of bounds after removal: " + position);
                }
            } else {
                Log.e("DeleteMedicineDetail", "Failed to delete the item with key: " + key);
                Toast.makeText(context, "Failed to delete medicine detail", Toast.LENGTH_SHORT).show();
            }
        });
    }
}