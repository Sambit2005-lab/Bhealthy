package com.codexnovas.bhealthy.OnboardingSlidePages;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codexnovas.bhealthy.R;


public class understand_your_health extends Fragment {


    public understand_your_health() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.understand_your_health, container, false);

        TextView skipButton = view.findViewById(R.id.textview5);
        AppCompatImageButton nextButton = view.findViewById(R.id.next_btn);

        ViewPager2 viewPager = ((onboarding_background) getActivity()).getViewPager();

        skipButton.setOnClickListener(v -> viewPager.setCurrentItem(3)); // Assuming 3 is the last fragment index
        nextButton.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1));


        return view;
    }
}