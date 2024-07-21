package com.codexnovas.bhealthy.HomeFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.codexnovas.bhealthy.NavHeader_Activity;
import com.codexnovas.bhealthy.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private TextView tempTextView;
    private TextView weatherTextView;
    private static final String API_KEY = "e09e69b5f8ee432a803173837241907";
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String userId;

    private FirebaseUser currentUser;

    private ImageView profilePic;

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tempTextView = view.findViewById(R.id.temp_text);
        weatherTextView = view.findViewById(R.id.weather_text);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());



        profilePic = view.findViewById(R.id.profile);


        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
            // Initialize Firebase Database
            databaseReference = FirebaseDatabase.getInstance().getReference("profile_pictures").child(userId);


            loadProfileImage();
        }
        // Check location permissions
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permissions already granted, get the location
            getLocationAndFetchWeather();
        }

        return view;
    }

    private void loadProfileImage() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference profileImageRef = databaseReference.child("profile_pictures/");

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
                                fetchWeather(locationString);
                            } else {
                                Log.e("Location", "Failed to get location");
                            }
                        }
                    });
        } catch (SecurityException e) {
            Log.e("Location", "Location permission error", e);
        }
    }

    private void fetchWeather(String location) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.weatherapi.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        com.codexnovas.bhealthy.HomeFragment.WeatherAPIService service = retrofit.create(com.codexnovas.bhealthy.HomeFragment.WeatherAPIService.class);
        Call<WeatherAPIResponse> call = service.getCurrentWeather(API_KEY, location);

        call.enqueue(new Callback<WeatherAPIResponse>() {
            @Override
            public void onResponse(Call<WeatherAPIResponse> call, Response<WeatherAPIResponse> response) {
                if (response.isSuccessful()) {
                    WeatherAPIResponse weatherResponse = response.body();
                    if (weatherResponse != null) {
                        float temperature = weatherResponse.getCurrent().getTempC();
                        String weatherDescription = weatherResponse.getCurrent().getCondition().getText();
                        String weatherText = "Temperature is " + temperature + "°C, " + weatherDescription;

                        tempTextView.setText(String.format("%d°C", (int) temperature));
                        weatherTextView.setText(weatherText);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndFetchWeather();
            } else {
                Log.e("Location", "Location permission denied");
            }
        }
    }
}