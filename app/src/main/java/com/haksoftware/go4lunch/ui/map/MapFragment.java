package com.haksoftware.go4lunch.ui.map;

import static com.haksoftware.go4lunch.repository.TodayRestaurantRepository.getTodayRestaurantByRestaurant;
import static com.haksoftware.go4lunch.repository.TodayRestaurantRepository.getTodayRestaurantCollection;
import static com.haksoftware.go4lunch.utils.Utils.createRestaurantFromResultObject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.haksoftware.go4lunch.BuildConfig;
import com.haksoftware.go4lunch.R;
import com.haksoftware.go4lunch.databinding.FragmentMapBinding;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.Result;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.Root;
import com.haksoftware.go4lunch.utils.ViewModelFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    private static final int REQUEST_CODE = 101;
    private static final String PERM_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PERM_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String TODAY_DATE = "date";
    private static final String TODAY_RESTAURANT_CHOSEN ="restaurant.name";

    public static final String GMAP_API_URL = "https://maps.googleapis.com/maps/api/place/";
    public static final int RADIUS = 1500;
    public static final String TYPE = "restaurant";
    private FragmentMapBinding binding;
    private GoogleMap gMap;
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private FusedLocationProviderClient fusedLocationClient;
    private MapViewModel mapViewModel;
    private MutableLiveData<Location> currentLocation = new MutableLiveData<>();
    private AutocompleteSupportFragment autocompleteSupportFragment;
    private double latitude, longitude;

    private List<Restaurant> restaurantList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapViewModel =
                new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);

        autocompleteSupportFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

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
        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        getCurrentLocation();

        MutableLiveData<List<Result>> result = new MutableLiveData<>();

        mapViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(MapViewModel.class);
        currentLocation.observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                updateNearbyRestaurant(location);
            }
        });

        if(!restaurantList.isEmpty()) {
            restaurantList.get(0).getAddress();
            Log.e("MAP", "onMapReady : " + restaurantList.get(0).getAddress());
        }
        else {
            Log.e("MAP", "onMapReady : empty list");
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to {@link MapFragment#onStart() Activity.onStart} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStart() {
        super.onStart();
        autocompleteSupportFragment.getView().setVisibility(View.INVISIBLE);

        // String stringLocation=currentLocation.getValue().getLatitude()+","+currentLocation.getValue().getLongitude();
        MutableLiveData<List<Result>> res = new MutableLiveData<>();
        currentLocation.observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                updateNearbyRestaurant(location);
                configureautocompleteSupportFragment(location);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        for(Restaurant restaurant : restaurantList) {
            LatLng restaurantCoordinates = new LatLng(getCoordinatesFromAddress(restaurant.getAddress()).latitude,
                    getCoordinatesFromAddress(restaurant.getAddress()).longitude);
            getTodayRestaurantByRestaurant(restaurant).addOnCompleteListener(task -> {
                MarkerOptions markerOptions = new MarkerOptions().position(restaurantCoordinates)
                        .title(restaurant.getName());
                if(task.getResult().size() != 0){
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    Log.e("TAG", "marherOptions is Green for " + restaurant.getName());
                }
                Marker marker = gMap.addMarker(markerOptions);
                marker.setTag(restaurant);
                gMap.moveCamera(CameraUpdateFactory.newLatLng(restaurantCoordinates));
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurantCoordinates, 150));
                gMap.setOnMarkerClickListener(marker1 -> {
                    Restaurant rest = (Restaurant) marker1.getTag();
                    Log.e("TAG", "onMarkerClick" + Objects.requireNonNull(rest).getAddress());
                    //TODO : Afficher le détail du restaurant
                    return true;
                });
            });
        }
    }

    private Location getCurrentLocation() {
        if(ActivityCompat.checkSelfPermission(requireContext(), PERM_ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(requireContext(),PERM_ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 60000)
                .setMinUpdateDistanceMeters(100)
                .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                .setWaitForAccurateLocation(true)
                .build();
        LocationCallback locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationRequest==null){
                    Log.e("currentlocation", "locationResult is null");
                }
                for (Location location:locationResult.getLocations() ){
                    if(location!=null){
                        currentLocation.postValue(location);
                        //  viewModel.setCurrentLocation(currentLocation);
                        Log.e("current LocationJson", "onLocationResult:list resto fragment "+location.getLongitude()+" Latitude "+location.getLatitude());
                    }
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null);
        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(requireActivity(), location -> {
            if(location!=null){
                latitude = location .getLatitude();
                longitude = location.getLongitude();
                currentLocation.postValue(location);
                LatLng latLng=new LatLng(latitude,
                        longitude);
                gMap.addMarker(
                        new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                                .title("You are here!," +latLng.toString()));
                System.out.println("getLatitude="+ latitude +"et " +
                        "getLongitude="+ longitude);
                gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            }
        });
        Location location = new Location("service Provider");
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        currentLocation.postValue(location);
        return location;
    }
    public final LatLng getCoordinatesFromAddress(String adresse) {
        Geocoder geocoder=new Geocoder(getContext());
        LatLng latlng = null;
        try {
            List<Address> adr=  geocoder.getFromLocationName(adresse,1);
            double lat=adr.get(0).getLatitude();
            double lng=adr.get(0).getLongitude();
            latlng= new LatLng(lat, lng);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return latlng;
    }
    public static Task<QuerySnapshot> getRestaurants(Restaurant restaurant){
        LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toLocalDate().atStartOfDay();
        return getTodayRestaurantCollection().whereEqualTo(TODAY_RESTAURANT_CHOSEN,restaurant.getName())
                .whereEqualTo(TODAY_DATE, date.toString())
                .get();
    }
    private void updateNearbyRestaurant(Location location) {
        MutableLiveData<List<Result>> res=new MutableLiveData<>();
        String stringLocation=location.getLatitude()+","+location.getLongitude();
        mapViewModel.getAllNearbyRestaurants(GMAP_API_URL, stringLocation, RADIUS, TYPE, BuildConfig.GOOGLE_MAPS_API_KEY)
                .enqueue(new Callback<Root>() {
                    @Override
                    public void onResponse(Call<Root> call, Response<Root> response) {
                        Root data = response.body();
                        System.out.println("isSuccessful "+response.isSuccessful());
                        res.postValue(data.getResults());
                        List <Restaurant> list=new ArrayList<>();
                        for(int i=0; i<data.getResults().size();i++) {
                            list.add(createRestaurantFromResultObject(data.getResults().get(i)));
                            System.out.println(" getBusinessStatus:" + data.getResults().get(i).getName() + "\n");
                        }
                        restaurantList.addAll(list);
                        for (Restaurant restaurant : restaurantList) {
                            LatLng retaurantLatLng = new LatLng(getCoordinatesFromAddress(restaurant.getAddress()).latitude,
                                    getCoordinatesFromAddress(restaurant.getAddress()).longitude);
                            getTodayRestaurantByRestaurant(restaurant).addOnCompleteListener(task -> {
                                MarkerOptions markerOptions = new MarkerOptions().position(retaurantLatLng)
                                        .title(restaurant.getName());
                                if (task.getResult().size()>0) {
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                    Log.e("TAG", "markerOptions is GREEN  FOR "+restaurant.getName());
                                }
                                Marker  marker= gMap.addMarker(markerOptions);
                                Objects.requireNonNull(marker).setTag(restaurant);
                                gMap.moveCamera(CameraUpdateFactory.newLatLng(retaurantLatLng));
                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(retaurantLatLng, 15));
                                gMap.setOnMarkerClickListener(marker1 -> {
                                    Restaurant restaurant1 = (Restaurant) marker1.getTag();
                                    Log.e("TAG", "onMarkerClick  "+ Objects.requireNonNull(restaurant1).getAddress());
                                    /*Intent intent = new Intent(getActivity(), DetailRestaurant.class);
                                    // Restaurant detailRestaurant=new Restaurant();
                                    intent.putExtra(KEY_RESTAURANT_ITEM, r);
                                    startActivity(intent);*/
                                    return true;

                                });
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<Root> call, Throwable t) {
                        System.out.println("onFailure "+t.fillInStackTrace());
                    }
                });
    }
    private void configureautocompleteSupportFragment(Location location) {
        String stringLocation=location.getLatitude()+","+location.getLongitude();
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.GOOGLE_MAPS_API_KEY);
        }
        PlacesClient placeClient = Places.createClient(requireContext());

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS,Place.Field.NAME));
        autocompleteSupportFragment.setHint("search restaurant");
        autocompleteSupportFragment.setCountries("FR");
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLng = place.getLatLng();
                Log.e("ERROR", "onPlaceSelected: " + latLng);
                Log.e("ERROR", "onPlaceSelected: " + place.getName());
                mapViewModel.getAllNearbyRestaurants(GMAP_API_URL, stringLocation, RADIUS, TYPE, BuildConfig.GOOGLE_MAPS_API_KEY).enqueue(new Callback<Root>() {
                    @Override
                    public void onResponse(Call<Root> call, Response<Root> response) {
                        Root data = response.body();
                        System.out.println("isSuccessful "+response.isSuccessful());
                        for(int i=0; i<data.getResults().size();i++) {
                            if(data.getResults().get(i).getName().equals(place.getName())){
                                /*restaurant = createRestaurantFromResultObject(data.getResults().get(i));
                                Intent monIntent = new Intent(getContext(), DetailRestaurant.class);
                                monIntent.putExtra(KEY_RESTAURANT_ITEM, restaurant);
                                getContext().startActivity(monIntent);*/
                                break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Root> call, Throwable t) {
                        System.out.println("onFailure "+t.fillInStackTrace());
                    }
                });
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.e("ERROR", "onPlaceSelected: " + status.getStatusMessage());

            }
        });

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        gMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }
}
