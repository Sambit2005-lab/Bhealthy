package com.codexnovas.bhealthy;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class engage_with_people extends Fragment {


    private AppCompatImageButton getStartedButton;

    public engage_with_people() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.engage_with_people, container, false);
        getStartedButton = view.findViewById(R.id.get_started_btn);

        getStartedButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),LoginPage.class);
            startActivity(intent);
        });


        return view;
    }
}