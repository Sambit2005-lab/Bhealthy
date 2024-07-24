package com.codexnovas.bhealthy;

public class MedicineDetails {

    private String dateTime;
    private String dosage;
    private String interval;
    private String medicineName;
    private String medicineType;
    private String specificTime;
    private String key;

    // Default constructor required for calls to DataSnapshot.getValue(MedicineDetails.class)
    public MedicineDetails() {
    }




    public MedicineDetails(String dateTime, String dosage, String interval, String medicineName, String medicineType, String specificTime,String key) {
        this.dateTime = dateTime;
        this.dosage = dosage;
        this.interval = interval;
        this.medicineName = medicineName;
        this.medicineType = medicineType;
        this.specificTime = specificTime;
        this.key = key;

    }

    // Getters and setters
    public String getDateTime() { return dateTime; }
    public String getDosage() { return dosage; }
    public String getInterval() { return interval; }
    public String getMedicineName() { return medicineName; }
    public String getMedicineType() { return medicineType; }
    public String getSpecificTime() { return specificTime; }

    public String getKey() { return key; } // Add this line

    public void setKey(String key) { this.key = key; } // Add this line

}
