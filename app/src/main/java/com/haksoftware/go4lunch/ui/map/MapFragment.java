package com.haksoftware.go4lunch.ui.map;

import static com.haksoftware.go4lunch.utils.Utils.createRestaurantFromResponseGMAP;
import static com.haksoftware.go4lunch.utils.Utils.generateBitmap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

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
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.haksoftware.go4lunch.BuildConfig;
import com.haksoftware.go4lunch.R;
import com.haksoftware.go4lunch.databinding.FragmentMapBinding;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.PlacesItem;
import com.haksoftware.go4lunch.utils.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final int REQUEST_CODE = 101;
    private static final String INTERNET = Manifest.permission.INTERNET;
    private static final String PERM_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PERM_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final int RADIUS = 500;

    private FragmentMapBinding binding;
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private MapViewModel mapViewModel;
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();
    private MutableLiveData<List<Restaurant>> restaurantListLiveData = new MutableLiveData<>();
    private final List<Restaurant> restaurantList = new ArrayList<>();
    //private AutocompleteSupportFragment autocompleteSupportFragment;

    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     *
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, add a {@link LifecycleObserver} on the
     * activity's Lifecycle, removing it when it receives the
     * @link Lifecycle.State#CREATED callback.
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

        // Initialize Places API
        Places.initialize(requireContext(), BuildConfig.GOOGLE_MAPS_API_KEY);

        mapViewModel =
                new ViewModelProvider(this, ViewModelFactory.getInstance(requireContext())).get(MapViewModel.class);

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
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        return binding.getRoot();
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        binding = null;
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
    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 600000)
                .setMinUpdateDistanceMeters(100)
                .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                .setWaitForAccurateLocation(true)
                .build();

        Looper looper = Looper.myLooper();
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, looper);

        getCurrentLocation();

/*        currentLocation.observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                if(location.getLatitude() != 0.0 && location.getLongitude() != 0.0)
                {
                    updateNearbyRestaurants();
                }
            }
        });*/
        mapViewModel.getRestaurantMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                // Mettez à jour votre UI ou effectuez d'autres actions en fonction des changements dans la liste des restaurants
            }
        });

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        //MapStyleOptions mapStyleOptions = new MapStyleOptions()
        try {
            // Customise the styling of the base map using a JSON object defined in a raw resource file.
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
        assert mapView != null;
        View locationButton = ((View) mapView.<View>findViewById(Integer.parseInt("1")).getParent()).<View>findViewById(Integer.parseInt("2"));

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 0, 260);

    }

    private void getCurrentLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            currentLocation.postValue(location);

                            LatLng latLng = new LatLng(latitude, longitude);
                            googleMap.addMarker(
                                    new MarkerOptions().position(latLng)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                            .title(getString(R.string.you_are_here)));

                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.5f));
                        } else {
                            // Handle the case where location is null
                            Log.e("Location", "Location is null");
                        }
                    })
                    .addOnFailureListener(requireActivity(), e -> {
                        // Handle failure in getting last location
                        Log.e("Location", "Failed to get last location: " + e.getMessage());
                    });
        }
    }

    private void updateNearbyRestaurants(Location location) {

        mapViewModel.getNearbyRestaurants(location.getLatitude(),
                location.getLongitude(), RADIUS).observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurantList) {
                showNearbyRestaurant(restaurantList);
            }
        });
    }
    private void showNearbyRestaurant(List<Restaurant> restaurantList){
        int pos = 0;
        for (Restaurant restaurant : restaurantList) {

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(restaurant.getLatLng())
                    .icon(BitmapDescriptorFactory.fromBitmap(generateBitmap(requireContext(), R.drawable.ic_restaurant_location, false)))
                    .title(String.valueOf(pos));
            Marker marker = googleMap.addMarker(markerOptions);
            assert marker != null;
            marker.showInfoWindow();

            pos++;
        }
        restaurantListLiveData.postValue(restaurantList);
        googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(requireContext(), restaurantList));
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            public void onInfoWindowClick(@NonNull Marker marker) {
                int pos = Integer.parseInt(Objects.requireNonNull(marker.getTitle()));
                Restaurant selectedRestaurant = restaurantList.get(pos);

                Bundle bundle = new Bundle();
                bundle.putParcelable("selectedRestaurant", selectedRestaurant);

                NavHostFragment.findNavController(MapFragment.this)
                        .navigate(R.id.action_MapFragment_to_RestaurantDetailFragment, bundle);
            }
        });
    }
    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), PERM_ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), PERM_ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), INTERNET) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET},
                    REQUEST_CODE);
            return false;
        }
    }
}