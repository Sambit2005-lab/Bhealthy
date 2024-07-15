package com.codexnovas.bhealthy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HealthMetrics extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private EditText heightEditText, weightEditText, bpSystolicEditText, bpDiastolicEditText, bloodSugarEditText, healthGoalsEditText;
    private AutoCompleteTextView heightUnitDropdown, weightUnitDropdown;
    private ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_metrics);

        // Initialize Firebase Auth and Database references
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize EditText fields
        heightEditText = findViewById(R.id.height_edit);
        weightEditText = findViewById(R.id.weight_edit);
        bpSystolicEditText = findViewById(R.id.bp_edit_1);
        bpDiastolicEditText = findViewById(R.id.bp_edit_2);
        bloodSugarEditText = findViewById(R.id.bs_edit);
        healthGoalsEditText = findViewById(R.id.goals_edit);

        // Initialize AutoCompleteTextViews
        heightUnitDropdown = findViewById(R.id.autocomplete_height);
        weightUnitDropdown = findViewById(R.id.autocomplete_weight);

        // Initialize ProgressBar
        progressBar = findViewById(R.id.progress_bar);

        // Set up the dropdown menus
        String[] heightUnits = {"cm", "feet"};
        ArrayAdapter<String> heightAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, heightUnits);
        heightUnitDropdown.setAdapter(heightAdapter);

        String[] weightUnits = {"kg", "pounds"};
        ArrayAdapter<String> weightAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, weightUnits);
        weightUnitDropdown.setAdapter(weightAdapter);

        // Example of saving data on button click
        findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMedicalData();
            }
        });
    }

    private void saveMedicalData() {
        progressBar.setVisibility(View.VISIBLE);

        String height = heightEditText.getText().toString();
        String heightUnit = heightUnitDropdown.getText().toString();
        String weight = weightEditText.getText().toString();
        String weightUnit = weightUnitDropdown.getText().toString();
        String bpSystolic = bpSystolicEditText.getText().toString();
        String bpDiastolic = bpDiastolicEditText.getText().toString();
        String bloodSugar = bloodSugarEditText.getText().toString();
        String healthGoals = healthGoalsEditText.getText().toString();

        // Get current user ID
        String userId = mAuth.getCurrentUser().getUid();

        // Create a UserMedicalData object
        UserMedicalData medicalData = new UserMedicalData(height, heightUnit, weight, weightUnit, bpSystolic, bpDiastolic, bloodSugar, healthGoals);

        // Save the data under the user's node in the database
        mDatabase.child("users").child(userId).child("healthMetrics").setValue(medicalData)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(HealthMetrics.this, "Medical data saved successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HealthMetrics.this, UploadPic.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(HealthMetrics.this, "Failed to save medical data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // UserMedicalData class to structure the data
    public static class UserMedicalData {
        public String height;
        public String heightUnit;
        public String weight;
        public String weightUnit;
        public String bpSystolic;
        public String bpDiastolic;
        public String bloodSugar;
        public String healthGoals;

        public UserMedicalData() {
            // Default constructor required for calls to DataSnapshot.getValue(UserMedicalData.class)
        }

        public UserMedicalData(String height, String heightUnit, String weight, String weightUnit, String bpSystolic, String bpDiastolic, String bloodSugar, String healthGoals) {
            this.height = height;
            this.heightUnit = heightUnit;
            this.weight = weight;
            this.weightUnit = weightUnit;
            this.bpSystolic = bpSystolic;
            this.bpDiastolic = bpDiastolic;
            this.bloodSugar = bloodSugar;
            this.healthGoals = healthGoals;
        }
    }
}