package com.codexnovas.bhealthy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class welcome_to_bhealthy extends Fragment {



    public welcome_to_bhealthy() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.welcome_to_bhealthy, container, false);
        return view;
    }
}