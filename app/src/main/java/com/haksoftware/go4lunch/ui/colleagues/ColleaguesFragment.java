package com.haksoftware.go4lunch.ui.colleagues;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.haksoftware.go4lunch.databinding.FragmentColleaguesBinding;

public class ColleaguesFragment extends Fragment {

    private FragmentColleaguesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ColleaguesViewModel notificationsViewModel =
                new ViewModelProvider(this).get(ColleaguesViewModel.class);

        binding = FragmentColleaguesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}