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
import com.haksoftware.go4lunch.utils.ViewModelFactory;

public class ColleaguesFragment extends Fragment {

    private FragmentColleaguesBinding binding;
    private ColleaguesListAdapter colleaguesListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ColleaguesViewModel colleaguesViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(this.requireActivity().getApplication())).get(ColleaguesViewModel.class);

        binding = FragmentColleaguesBinding.inflate(inflater, container, false);

        RecyclerView colleaguesRecyclerView = binding.colleaguesRecyclerView;
        colleaguesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        colleaguesListAdapter = new ColleaguesListAdapter();
        colleaguesRecyclerView.setAdapter(colleaguesListAdapter);
        colleaguesViewModel.getColleagues().observe(getViewLifecycleOwner(), colleagueList ->
                colleaguesListAdapter.submitList(colleagueList));
        return binding.getRoot();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}