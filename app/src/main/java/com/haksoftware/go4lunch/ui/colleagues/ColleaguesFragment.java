package com.haksoftware.go4lunch.ui.colleagues;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haksoftware.go4lunch.databinding.FragmentColleaguesBinding;
import com.haksoftware.go4lunch.ui.restaurant_list.RestaurantListViewAdapter;
import com.haksoftware.go4lunch.utils.ViewModelFactory;

public class ColleaguesFragment extends Fragment {

    private FragmentColleaguesBinding binding;
    private RecyclerView colleaguesRecyclerView;
    private ColleaguesListAdapter colleaguesListAdapter;
    private ColleaguesViewModel colleaguesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        colleaguesViewModel =
                new ViewModelProvider(this,ViewModelFactory.getInstance(requireContext())).get(ColleaguesViewModel.class);

        binding = FragmentColleaguesBinding.inflate(inflater, container, false);

        colleaguesRecyclerView = binding.colleaguesRecyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        colleaguesRecyclerView.setLayoutManager(layoutManager);
        colleaguesListAdapter = new ColleaguesListAdapter();
        colleaguesRecyclerView.setAdapter(colleaguesListAdapter);

        colleaguesViewModel.getColleagues().observe(getViewLifecycleOwner(), colleagueList -> {
            colleaguesListAdapter.submitList(colleagueList);

        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}