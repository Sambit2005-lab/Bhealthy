package com.codexnovas.bhealthy.HomeFragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.codexnovas.bhealthy.R;
import com.codexnovas.bhealthy.HomeFragment.SunriseSunsetCalculator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tempTextView = view.findViewById(R.id.temp_text);
        weatherTextView = view.findViewById(R.id.weather_text);
        weatherIconImageView = view.findViewById(R.id.weather_image);
        weatherLottieView = view.findViewById(R.id.weather_gif);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

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
            case "light rain":
            case "rain":
                animationResource = isDayTime ? R.raw.rainy_day : R.raw.rain_backgroundmp4lottie;
                break;
            case "sunny":
                animationResource = R.raw.sunny_day;
                break;
            case "cloudy":
                animationResource = isDayTime ? R.raw.cloudy_day : R.raw.cloudy_night;
                break;
            case "mist":
            case "fog":
                animationResource = isDayTime ? R.raw.foggy_day : R.raw.foggy_night;
                break;
            // if any other weather condition is found, I will add here THE animation
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
        Calendar sunrise = SunriseSunsetCalculator.getSunrise(calendar, latitude, longitude);
        Calendar sunset = SunriseSunsetCalculator.getSunset(calendar, latitude, longitude);

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
    }}