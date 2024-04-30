package com.haksoftware.go4lunch.ui.restaurant_list;

import static com.haksoftware.go4lunch.utils.Utils.getRestaurantFromPlace;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.haksoftware.go4lunch.R;
import com.haksoftware.go4lunch.databinding.FragmentRestaurantsBinding;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.ui.map.MapViewModel;
import com.haksoftware.go4lunch.utils.RestaurantDistanceComparator;
import com.haksoftware.go4lunch.utils.ViewModelFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RestaurantsFragment extends Fragment {

    private FragmentRestaurantsBinding binding;
    private MapViewModel mapViewModel;
    private RestaurantListViewAdapter restaurantListViewAdapter;
    private View autoCompleteView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mapViewModel =
                new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance(requireActivity().getApplication())).get(MapViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRestaurantsBinding.inflate(inflater, container, false);

        RecyclerView restaurantsRecyclerView = binding.restaurantsRecyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        restaurantsRecyclerView.setLayoutManager(layoutManager);
        restaurantListViewAdapter = new RestaurantListViewAdapter();
        restaurantsRecyclerView.setAdapter(restaurantListViewAdapter);

        mapViewModel.getRestaurantMutableLiveData().observe(getViewLifecycleOwner(), restaurantList -> restaurantListViewAdapter.submitList(restaurantList));

        initAutocompleteFragment();

        return binding.getRoot();
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        requireActivity().getMenuInflater().inflate(R.menu.restaurants_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_search){
            if(autoCompleteView.getVisibility() == View.VISIBLE) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.restaurantsRecyclerView.getLayoutParams();
                layoutParams.setMargins(0, 156, 0, 0);
                binding.restaurantsRecyclerView.setLayoutParams(layoutParams);
                autoCompleteView.setVisibility(View.GONE);
            }
            else {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.restaurantsRecyclerView.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, 0);
                binding.restaurantsRecyclerView.setLayoutParams(layoutParams);
                autoCompleteView.setVisibility(View.VISIBLE);
            }
        } else if (item.getItemId() == R.id.item_sort) {
            updateRestaurantList(mapViewModel.getRestaurantMutableLiveData().getValue());
        }
        else {
            AppCompatActivity activity = (AppCompatActivity) requireActivity();
            DrawerLayout drawerLayout = activity.findViewById(R.id.main_layout);
            drawerLayout.open();
        }
        return true;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void initAutocompleteFragment() {

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        if (autocompleteFragment != null) {
            autoCompleteView = autocompleteFragment.getView();

            if( autoCompleteView != null) {
                autoCompleteView.setVisibility(View.GONE);

                autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.PHONE_NUMBER,
                        Place.Field.RATING,
                        Place.Field.TYPES,
                        Place.Field.PHOTO_METADATAS,
                        Place.Field.ADDRESS,
                        Place.Field.CURRENT_OPENING_HOURS,
                        Place.Field.EDITORIAL_SUMMARY,
                        Place.Field.WEBSITE_URI,
                        Place.Field.LAT_LNG));
                autocompleteFragment.setActivityMode(AutocompleteActivityMode.OVERLAY);
                autocompleteFragment.setHint("search restaurant");
                autocompleteFragment.setCountries("FR");
                autocompleteFragment.setTypesFilter(Collections.singletonList(PlaceTypes.RESTAURANT));
            }
        }

        Objects.requireNonNull(autocompleteFragment).setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Restaurant restaurant = getRestaurantFromPlace(requireContext(),place);
                Bundle bundle = new Bundle();
                bundle.putParcelable("selectedRestaurant", restaurant);

                NavHostFragment.findNavController(RestaurantsFragment.this)
                        .navigate(R.id.action_RestaurantsFragment_to_RestaurantDetailFragment, bundle);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.e("Error", "onPlaceSelected: " + status.getStatusMessage());

            }
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    private void updateRestaurantList(List<Restaurant> restaurantList) {
        Collections.sort(restaurantList, new RestaurantDistanceComparator());

        restaurantListViewAdapter.submitList(restaurantList);
        restaurantListViewAdapter.notifyDataSetChanged();
    }

}
