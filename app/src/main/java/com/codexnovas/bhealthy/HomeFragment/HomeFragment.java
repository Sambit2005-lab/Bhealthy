package com.codexnovas.bhealthy.HomeFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.codexnovas.bhealthy.AboutFragment;
import com.codexnovas.bhealthy.ContactFragment;
import com.codexnovas.bhealthy.FeedbackFragment;
import com.codexnovas.bhealthy.LogoutFragment;
import com.codexnovas.bhealthy.MedicineAdapter;
import com.codexnovas.bhealthy.MedicineCard;
import com.codexnovas.bhealthy.MedicineDetails;
import com.codexnovas.bhealthy.R;
import com.codexnovas.bhealthy.HomeFragment.SunriseSunsetCalculator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private TextView tempTextView;
    private TextView weatherTextView;
    private ImageView weatherIconImageView;
    private LottieAnimationView weatherLottieView;
    private static final String API_KEY = "e09e69b5f8ee432a803173837241907";
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String userId;

    private FirebaseUser currentUser;

    private ImageView profilePic;

    private TextView setMedicine;

    private RecyclerView recyclerView;
    private MedicineAdapter medicineAdapter;
    private List<MedicineDetails> medicineList = new ArrayList<>();




    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        tempTextView = view.findViewById(R.id.temp_text);
        weatherTextView = view.findViewById(R.id.weather_text);
        weatherIconImageView = view.findViewById(R.id.weather_image);
        weatherLottieView = view.findViewById(R.id.weather_gif);
        AppCompatImageButton menuBtn=view.findViewById(R.id.nav_drawer);
        setMedicine=view.findViewById(R.id.set_med_reminder_text);

        setMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MedicineCard.class);
                startActivity(intent);
            }
        });

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.med_alert_card_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());


          profilePic = view.findViewById(R.id.profile);


        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
            // Initialize Firebase Database
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);


            loadProfileImage();
        }




        medicineList = new ArrayList<>();
        medicineAdapter = new MedicineAdapter(getContext(), medicineList);
        recyclerView.setAdapter(medicineAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("medicineDetails");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("FirebaseData", "Child Added - Key: " + snapshot.getKey() + ", Value: " + snapshot.getValue());
                MedicineDetails medicine = snapshot.getValue(MedicineDetails.class);
                if (medicine != null) {
                    medicine.setKey(snapshot.getKey());
                    medicineList.add(medicine);
                    medicineAdapter.notifyItemInserted(medicineList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("FirebaseData", "Child Changed - Key: " + snapshot.getKey() + ", Value: " + snapshot.getValue());
                MedicineDetails updatedMedicine = snapshot.getValue(MedicineDetails.class);
                if (updatedMedicine != null) {
                    String key = snapshot.getKey();
                    for (int i = 0; i < medicineList.size(); i++) {
                        if (medicineList.get(i).getKey().equals(key)) {
                            updatedMedicine.setKey(key);
                            medicineList.set(i, updatedMedicine);
                            medicineAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d("FirebaseData", "Child Removed - Key: " + snapshot.getKey() + ", Value: " + snapshot.getValue());
                String key = snapshot.getKey();
                for (int i = 0; i < medicineList.size(); i++) {
                    if (medicineList.get(i).getKey().equals(key)) {
                        medicineList.remove(i);
                        medicineAdapter.notifyItemRemoved(i);
                        medicineAdapter.notifyItemRangeChanged(i, medicineList.size());
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle child moved if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
                Log.e("FirebaseData", "DatabaseError: " + error.getMessage());
            }
        };

        databaseReference.addChildEventListener(childEventListener);


        // Check location permissions
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permissions already granted, check if location services are enabled
            if (isLocationEnabled()) {
                // Location services enabled, get the location
                getLocationAndFetchWeather();
            } else {
                Log.e("Location", "Location services are disabled");
            }
        }


        return view;

    }


    private void loadProfileImage() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference profileImageRef = databaseReference.child("profileImage");

            profileImageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String profileImageUrl = dataSnapshot.getValue(String.class);
                        Glide.with(HomeFragment.this)
                                .load(profileImageUrl)
                                .circleCrop()
                                .into(profilePic);
                    } else {
                        Toast.makeText(getContext(), "Profile image not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to load profile image: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void getLocationAndFetchWeather() {
        try {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                String locationString = latitude + "," + longitude;
                                fetchWeather(locationString, latitude, longitude);
                            } else {
                                Log.e("Location", "Failed to get location");
                            }
                        }
                    })
                    .addOnFailureListener(requireActivity(), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Location", "Failed to get location", e);
                        }
                    });
        } catch (SecurityException e) {
            Log.e("Location", "Location permission error", e);
        }
    }

    private void fetchWeather(String location, double latitude, double longitude) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.weatherapi.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherAPIService service = retrofit.create(WeatherAPIService.class);
        Call<WeatherAPIResponse> call = service.getCurrentWeather(API_KEY, location);

        call.enqueue(new Callback<WeatherAPIResponse>() {
            @Override
            public void onResponse(Call<WeatherAPIResponse> call, Response<WeatherAPIResponse> response) {
                if (response.isSuccessful()) {
                    WeatherAPIResponse weatherResponse = response.body();
                    if (weatherResponse != null) {
                        float temperature = weatherResponse.getCurrent().getTempC();
                        String weatherDescription = weatherResponse.getCurrent().getCondition().getText();
                        String iconUrl = "https:" + weatherResponse.getCurrent().getCondition().getIcon();
                        String weatherText = "Temperature is " + temperature + "°C, " + weatherDescription;

                        tempTextView.setText(String.format("%d°C", (int) temperature));
                        weatherTextView.setText(weatherText);
                        Picasso.get().load(iconUrl).into(weatherIconImageView);

                        setWeatherAnimation(weatherDescription, latitude, longitude);
                        Log.i("WeatherAPI", weatherText);
                    }
                } else {
                    Log.e("WeatherAPI", "Weather API call failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WeatherAPIResponse> call, Throwable t) {
                Log.e("WeatherAPI", "Weather API call failed", t);
            }
        });
    }

    private void setWeatherAnimation(String weatherCondition, double latitude, double longitude) {
        boolean isDayTime = isDayTime(latitude, longitude);
        int animationResource;
        switch (weatherCondition.toLowerCase()) {
            case "clear":
                animationResource = isDayTime ? R.raw.normal_day : R.raw.normal_night;
                break;
            case "Light rain":
            case "rain":
                animationResource = isDayTime ? R.raw.rainy_day : R.raw.rain_backgroundmp4lottie;
                break;
            case "sunny":
                animationResource = R.raw.normal_day;
                break;
            case "cloudy":
                animationResource = isDayTime ? R.raw.rainy_day : R.raw.cloudy_night;
                break;
            case "mist":
            case "fog":
                animationResource = isDayTime ? R.raw.foggy_day : R.raw.foggy_night;
                break;
            // Add other cases as needed
            default:
                animationResource = isDayTime ? R.raw.normal_day : R.raw.normal_night;
                break;
        }
        weatherLottieView.setAnimation(animationResource);
        weatherLottieView.playAnimation();
    }

    private boolean isDayTime(double latitude, double longitude) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        TimeZone timeZone = calendar.getTimeZone();
        Calendar sunrise = SunriseSunsetCalculator.getSunrise(calendar, latitude, longitude, timeZone);
        Calendar sunset = SunriseSunsetCalculator.getSunset(calendar, latitude, longitude, timeZone);

        return calendar.after(sunrise) && calendar.before(sunset);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {
                    getLocationAndFetchWeather();
                } else {
                    Log.e("Location", "Location services are disabled");
                }
            } else {
                Log.e("Location", "Location permission denied");
            }
        }
    }
}
