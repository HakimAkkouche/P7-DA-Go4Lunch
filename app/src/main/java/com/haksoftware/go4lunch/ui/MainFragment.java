package com.haksoftware.go4lunch.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haksoftware.go4lunch.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {



    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentMainBinding binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}