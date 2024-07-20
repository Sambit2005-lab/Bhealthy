package com.codexnovas.bhealthy.HomeFragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.codexnovas.bhealthy.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tempTextView = view.findViewById(R.id.temp_text);
        weatherTextView = view.findViewById(R.id.weather_text);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

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

        com.codexnovas.bhealthy.network.WeatherAPIService service = retrofit.create(com.codexnovas.bhealthy.network.WeatherAPIService.class);
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