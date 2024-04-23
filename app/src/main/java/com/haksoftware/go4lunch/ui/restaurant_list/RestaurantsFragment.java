package com.haksoftware.go4lunch.ui.restaurant_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.haksoftware.go4lunch.databinding.FragmentListViewBinding;

public class RestaurantsFragment extends Fragment {

    private FragmentListViewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RestaurantListViewViewModel restaurantListViewViewModel =
                new ViewModelProvider(this).get(RestaurantListViewViewModel.class);

        binding = FragmentListViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        restaurantListViewViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}