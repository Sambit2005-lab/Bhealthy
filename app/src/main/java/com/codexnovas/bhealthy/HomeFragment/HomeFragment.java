package com.codexnovas.bhealthy.HomeFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codexnovas.bhealthy.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private TextView tempTextView;
    private TextView weatherTextView;
    private static final String API_KEY = "e09e69b5f8ee432a803173837241907";  // Replace with your actual WeatherAPI key

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tempTextView = view.findViewById(R.id.temp_text);
        weatherTextView = view.findViewById(R.id.weather_text);

        fetchWeather("Bhubaneswar"); // You can change the location as needed

        return view;
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
}
