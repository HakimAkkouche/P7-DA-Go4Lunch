package com.haksoftware.go4lunch.ui.map;

import static com.haksoftware.go4lunch.utils.Utils.generateBitmap;
import static com.haksoftware.go4lunch.utils.Utils.getRestaurantFromPlace;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.haksoftware.go4lunch.BuildConfig;
import com.haksoftware.go4lunch.R;
import com.haksoftware.go4lunch.databinding.FragmentMapBinding;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.utils.ViewModelFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class MapFragment extends Fragment implements OnMapReadyCallback, ColleaguesCountLoadedCallback {
    private static final int REQUEST_CODE = 101;
    private static final String PERM_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PERM_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String INTERNET = Manifest.permission.INTERNET;
    public static final int RADIUS = 1500;
    private FragmentMapBinding binding;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private MapViewModel mapViewModel;
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();
    private View autoCompleteView;
    private SupportMapFragment mapFragment;
    private List<Restaurant> restaurantList = new ArrayList<>();

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Places.initialize(requireContext(), BuildConfig.GOOGLE_MAPS_API_KEY);
        setHasOptionsMenu(true);

        mapViewModel =
                new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance(requireActivity().getApplication())).get(MapViewModel.class);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    currentLocation.postValue(location);
                    updateNearbyRestaurants(location);
                }
            }
        };
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMapBinding.inflate(inflater, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if(mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        requireActivity().setTitle(R.string.toolbar_title_home);
        return binding.getRoot();
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        getCurrentLocation();
        setAutocompleteFragment();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        binding = null;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_search){

            RelativeLayout.LayoutParams layoutParamsMap = (RelativeLayout.LayoutParams) mapFragment.requireView().getLayoutParams();
            if(autoCompleteView.getVisibility() == View.VISIBLE) {
                layoutParamsMap.setMargins(0, 156, 0, 0);
                mapFragment.requireView().setLayoutParams(layoutParamsMap);
                autoCompleteView.setVisibility(View.GONE);
            }
            else {
                layoutParamsMap.setMargins(0, 0, 0, 0);
                mapFragment.requireView().setLayoutParams(layoutParamsMap);
                RelativeLayout.LayoutParams layoutParamsAuto = (RelativeLayout.LayoutParams) autoCompleteView.getLayoutParams();
                layoutParamsAuto.setMargins(0,156,0, 0);
                autoCompleteView.setLayoutParams(layoutParamsAuto);
                autoCompleteView.setVisibility(View.VISIBLE);
            }
        }
        else {
            AppCompatActivity activity = (AppCompatActivity) requireActivity();
            DrawerLayout drawerLayout = activity.findViewById(R.id.main_layout);
            drawerLayout.open();
        }
        return true;
    }

    private void setAutocompleteFragment() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        if(autocompleteFragment != null) {
            autoCompleteView = autocompleteFragment.getView();
            if (autoCompleteView != null) {
                autoCompleteView.setVisibility(View.INVISIBLE);
            }


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
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    Restaurant restaurant = getRestaurantFromPlace(requireContext(), place);
                    Objects.requireNonNull(mapViewModel.getRestaurantMutableLiveData().getValue()).add(restaurant);
                    restaurantList.add(restaurant);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(restaurant.getLatitude(), restaurant.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromBitmap(generateBitmap(requireContext(), R.drawable.ic_restaurant_location, false)))
                            .snippet(String.valueOf(mapViewModel.getRestaurantMutableLiveData().getValue().size()-1));
                    Marker marker = googleMap.addMarker(markerOptions);
                    if (marker != null) {
                        marker.showInfoWindow();
                    }
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(restaurant.getLatitude(), restaurant.getLongitude()),15.5f));
                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.e("Error", "onPlaceSelected: " + status.getStatusMessage());

                }
            });

        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            Objects.requireNonNull(requireActivity()).getApplicationContext(), R.raw.mapstyle));

            if (!success) {
                Log.e("TAG", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("TAG", "Can't find style. Error: ", e);
        }

        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        this.googleMap.setMyLocationEnabled(true);

        View mapView = mapFragment.getView();
        if(mapView != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();

            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 0, 260);
        }
    }

    private void getCurrentLocation() {
        if (checkLocationPermission()) {

            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 600000)
                    .setMinUpdateDistanceMeters(100)
                    .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                    .setWaitForAccurateLocation(true)
                    .build();
            Looper looper = Looper.myLooper();
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, looper);

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            currentLocation.postValue(location);
                            LatLng latLng = new LatLng(latitude, longitude);


                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.5f));
                        } else {
                            Log.e("Location", "Location is null");
                        }
                    })
                    .addOnFailureListener(requireActivity(), e ->
                            Log.e("Location", "Failed to get last location: " + e.getMessage()));
        }
    }
    private void updateNearbyRestaurants(Location location) {
        mapViewModel.getNearbyRestaurants(location.getLatitude(),
                location.getLongitude(), RADIUS, this).observe(getViewLifecycleOwner(), show -> {
            showNearbyRestaurant();
        });
    }

    private void showNearbyRestaurant(){
        int pos = 0;
        for (Restaurant restaurant : restaurantList) {
            boolean hasColleagues = restaurant.getColleaguesCount() > 0;
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(restaurant.getLatitude(), restaurant.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(generateBitmap(requireContext(), R.drawable.ic_restaurant_location, hasColleagues)))
                    .snippet(String.valueOf(pos));
            googleMap.addMarker(markerOptions);
            pos++;
        }
        googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(requireContext(), restaurantList));
        googleMap.setOnInfoWindowClickListener(marker -> {
            int pos1 = Integer.parseInt(Objects.requireNonNull(marker.getSnippet()));
            Restaurant selectedRestaurant = restaurantList.get(pos1);

            Bundle bundle = new Bundle();
            bundle.putParcelable("selectedRestaurant", selectedRestaurant);

            NavHostFragment.findNavController(MapFragment.this)
                    .navigate(R.id.action_MapFragment_to_RestaurantDetailFragment, bundle);
        });
    }
    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), PERM_ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), PERM_ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), INTERNET) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET},
                    REQUEST_CODE);
            return false;
        }
    }

    @Override
    public void colleaguesCountLoaded(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
        showNearbyRestaurant();
    }
}
