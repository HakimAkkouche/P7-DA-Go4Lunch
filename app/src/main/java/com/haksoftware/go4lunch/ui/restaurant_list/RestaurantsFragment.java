package com.haksoftware.go4lunch.ui.restaurant_list;

import static com.haksoftware.go4lunch.utils.Utils.createRestaurantFromPlace;
import static com.haksoftware.go4lunch.utils.Utils.getCoordinate;

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
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.haksoftware.go4lunch.R;
import com.haksoftware.go4lunch.databinding.FragmentRestaurantsBinding;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.utils.RestaurantDistanceComparator;
import com.haksoftware.go4lunch.utils.ViewModelFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RestaurantsFragment extends Fragment {

    private final static int RADIUS = 500;
    private FragmentRestaurantsBinding binding;
    private RestaurantListViewViewModel restaurantListViewViewModel;
    private RestaurantListViewAdapter restaurantListViewAdapter;
    private AutocompleteSupportFragment autocompleteFragment;
    private View autoCompleteView;

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
        setHasOptionsMenu(true);

        restaurantListViewViewModel =
                new ViewModelProvider(this, ViewModelFactory.getInstance(requireContext())).get(RestaurantListViewViewModel.class);
    }

    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRestaurantsBinding.inflate(inflater, container, false);

        RecyclerView restaurantsRecyclerView = binding.restaurantsRecyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        restaurantsRecyclerView.setLayoutManager(layoutManager);
        restaurantListViewAdapter = new RestaurantListViewAdapter();
        restaurantsRecyclerView.setAdapter(restaurantListViewAdapter);

        restaurantListViewViewModel.getRestaurantViewStateMLD().observe(getViewLifecycleOwner(), restaurantList -> {
            for (RestaurantViewState restaurantViewState : restaurantList) {
                restaurantViewState.getDistance().observe(getViewLifecycleOwner(), restaurantViewState::setDistance);
            }

            restaurantListViewAdapter.submitList(restaurantList);
        });
        restaurantListViewViewModel.getIsDistanceLoaded().observe(getViewLifecycleOwner(), isDistanceLoaded ->
                restaurantListViewViewModel.getIsCountLoaded().observe(getViewLifecycleOwner(), isCountLoaded -> {
                    if(restaurantListViewViewModel.getRestaurantViewStateMLD().getValue() != null) {
                        for (RestaurantViewState restaurantViewState : restaurantListViewViewModel.getRestaurantViewStateMLD().getValue()) {
                            if (restaurantViewState.getDistance() == null) {
                                return;
                            }
                        }
                    }
                    restaurantListViewAdapter.notifyDataSetChanged();
                }));


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
            updateRestaurantList(restaurantListViewViewModel.getRestaurantViewStateMLD().getValue());
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

        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        if (autocompleteFragment != null) {
            autoCompleteView = autocompleteFragment.getView();

            if( autoCompleteView != null) {
                autoCompleteView.setVisibility(View.GONE);


                if(autocompleteFragment != null) {
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
        }
        restaurantListViewViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), location ->
                autocompleteFragment.setLocationRestriction(RectangularBounds.newInstance(
                getCoordinate(location.getLatitude(), location.getLongitude(), RADIUS))));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Restaurant restaurant = createRestaurantFromPlace(requireContext(),place);
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
    private void updateRestaurantList(List<RestaurantViewState> restaurantList) {
        Collections.sort(restaurantList, new RestaurantDistanceComparator());

        restaurantListViewAdapter.submitList(restaurantList);
        restaurantListViewAdapter.notifyDataSetChanged();
    }

}
