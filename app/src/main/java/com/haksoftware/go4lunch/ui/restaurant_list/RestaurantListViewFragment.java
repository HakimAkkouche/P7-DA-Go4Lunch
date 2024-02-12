package com.haksoftware.go4lunch.ui.restaurant_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haksoftware.go4lunch.databinding.FragmentRestaurantListBinding;
import com.haksoftware.go4lunch.utils.ViewModelFactory;

public class RestaurantListViewFragment extends Fragment {

    private FragmentRestaurantListBinding binding;
    private RestaurantListViewViewModel restaurantListViewViewModel;
    private RecyclerView restaurantsRecyclerView;
    private RestaurantListViewAdapter restaurantListViewAdapter;

    /**
     * Called to do initial creation of a fragment.
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     *
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.
     *
     * <p>Any restored child fragments will be created before the base
     * <code>Fragment.onCreate</code> method returns.</p>
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restaurantListViewViewModel =
                new ViewModelProvider(this, ViewModelFactory.getInstance(requireContext())).get(RestaurantListViewViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRestaurantListBinding.inflate(inflater, container, false);

        restaurantsRecyclerView = binding.restaurantsRecyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        restaurantsRecyclerView.setLayoutManager(layoutManager);
        restaurantListViewAdapter = new RestaurantListViewAdapter();
        restaurantsRecyclerView.setAdapter(restaurantListViewAdapter);

        restaurantListViewViewModel.getRestaurantViewStateMLD().observe(getViewLifecycleOwner(), restaurantList -> {
            restaurantListViewAdapter.submitList(restaurantList);

        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}