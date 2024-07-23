package com.codexnovas.bhealthy;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MedicineCard extends AppCompatActivity {

    private EditText medEditText, dosageEditText;
    private AppCompatButton confirmButton, selectTimeAndDateBtn;
    private Spinner intervalSpinner, timeSpinner;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String medType = "";
    private String selectedInterval, selectedTime;
    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicine_card);

        medEditText = findViewById(R.id.med_name_edit);
        dosageEditText = findViewById(R.id.dosage_edit);
        confirmButton = findViewById(R.id.confirm_btn);
        selectTimeAndDateBtn = findViewById(R.id.select_time_btn);
        intervalSpinner = findViewById(R.id.selected_interval); // Add this line
        timeSpinner = findViewById(R.id.selected_time); // Add this line


        // Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("medicineDetails");
        }

        // Interval Spinner setup
        ArrayAdapter<CharSequence> intervalAdapter = ArrayAdapter.createFromResource(this,
                R.array.interval_array, android.R.layout.simple_spinner_item);
        intervalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intervalSpinner.setAdapter(intervalAdapter);

        intervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedInterval = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Time Spinner setup
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(this,
                R.array.time_array, android.R.layout.simple_spinner_item);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeAdapter);

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTime = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        selectTimeAndDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
                showTimePicker();
            }
        });

        // Medicine Type selection (CardViews)
        findViewById(R.id.bottle_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medType = "Bottle";
                Toast.makeText(getApplicationContext(), "Bottle selected", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.pill_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medType = "";
                Toast.makeText(getApplicationContext(), "pill selected", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.syringe_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medType = "Syringe";
                Toast.makeText(getApplicationContext(), "Syringe selected", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.tablet_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medType = "Tablet";
                Toast.makeText(getApplicationContext(), "Tablet selected", Toast.LENGTH_SHORT).show();
            }
        });

        // Confirm Button to save data
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMedicineDetails();
            }
        });



    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedYear = year;
                selectedMonth = month;
                selectedDay = dayOfMonth;
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectedHour = hourOfDay;
                selectedMinute = minute;
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }


    private void saveMedicineDetails() {
        String medName =  medEditText.getText().toString().trim();
        String dosage = dosageEditText.getText().toString().trim();

        if (medName.isEmpty() || dosage.isEmpty() || medType.isEmpty() || selectedInterval == null || selectedTime == null) {
            Toast.makeText(MedicineCard.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
        long dateTimeMillis = calendar.getTimeInMillis();
        String dateTime = String.format("%02d/%02d/%d %02d:%02d", selectedDay, selectedMonth + 1, selectedYear, selectedHour, selectedMinute);

        Map<String, String> medicineDetails = new HashMap<>();
        medicineDetails.put("medicineName", medName);
        medicineDetails.put("dosage", dosage);
        medicineDetails.put("medicineType", medType);
        medicineDetails.put("interval", selectedInterval);
        medicineDetails.put("specificTime", selectedTime);
        medicineDetails.put("dateTime", dateTime);

        databaseReference.push().setValue(medicineDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MedicineCard.this, "Medicine details saved", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            } else {
                Toast.makeText(MedicineCard.this, "Failed to save medicine details", Toast.LENGTH_SHORT).show();
            }
        });

    }

}



