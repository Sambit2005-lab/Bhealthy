package com.codexnovas.bhealthy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class welcome_to_bhealthy extends Fragment {



    public welcome_to_bhealthy() {
        // Required empty public constructor
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.welcome_to_bhealthy, container, false);

        TextView skipButton = view.findViewById(R.id.skip_text);
        AppCompatImageButton nextButton = view.findViewById(R.id.next_btn);

        ViewPager2 viewPager = ((onboarding_background) getActivity()).getViewPager();

        skipButton.setOnClickListener(v -> viewPager.setCurrentItem(3)); // Assuming 3 is the last fragment index
        nextButton.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1));

        return view;



    }

}