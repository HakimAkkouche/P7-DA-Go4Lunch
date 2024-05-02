package com.haksoftware.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.app.Application;
import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.FirebaseUser;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.DisplayName;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.EditorialSummary;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.Location;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.PhotosItem;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.PlacesItem;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.ResponseGMAP;
import com.haksoftware.go4lunch.repository.FirebaseRepository;
import com.haksoftware.go4lunch.repository.GmapRepository;
import com.haksoftware.go4lunch.ui.map.ColleaguesCountLoadedCallback;
import com.haksoftware.go4lunch.ui.map.MapViewModel;
import com.haksoftware.go4lunch.utils.RestaurantSelectedCallback;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @InjectMocks
    FirebaseRepository firebaseRepository;
    @Mock
    ColleaguesCountLoadedCallback callback;
    GmapRepository gmapRepository;
    MapViewModel mapViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        firebaseRepository = mock(FirebaseRepository.class);
        gmapRepository = mock((GmapRepository.class));
        mapViewModel = new MapViewModel(mock(Application.class), firebaseRepository, gmapRepository);

    }

    @Test
    public void testGetCurrentUser() {
        FirebaseUser user = mock(FirebaseUser.class);
        when(firebaseRepository.getCurrentUser()).thenReturn(user);

        FirebaseUser result = mapViewModel.getCurrentUser();

        assertEquals(user, result);
    }
    @Test
    public void testGetNearbyRestaurants() {
        List<PlacesItem> mockResults = createPlacesItem();
        Context context = mock(Context.class);
        when(context.getString(R.string.opening_hours)).thenReturn("Your simulated string");

        ResponseGMAP responseGMAP = mock(ResponseGMAP.class);
        when(responseGMAP.getPlaces()).thenReturn(mockResults);

        Call<ResponseGMAP> call = mock(Call.class);

        doAnswer(invocation -> {
            Callback<ResponseGMAP> callback = invocation.getArgument(0);
            callback.onResponse(call, Response.success(responseGMAP));
            return null;
        }).when(call).enqueue(any(Callback.class));

        when(gmapRepository.getNearbyRestaurants(anyDouble(), anyDouble(), anyInt())).thenReturn(call);

        MutableLiveData<List<Restaurant>> resultLiveData = mapViewModel.getNearbyRestaurants(48.85849006983193, 2.294438381937397, 500, callback);

        assertNotNull(resultLiveData);

        Observer<List<Restaurant>> observer = restaurantList -> assertFalse(restaurantList.isEmpty());

        resultLiveData.observeForever(observer);
    }

    @Test
    public void testGetSelectedRestaurant() {
        Restaurant mockRestaurant = new Restaurant("id", "Mock Restaurant", "0602", 4.3f, "", "", "", "", "", "", 2.3, 2.3);

        MutableLiveData<Restaurant> selectedRestaurantLiveData = new MutableLiveData<>();
        selectedRestaurantLiveData.setValue(mockRestaurant);

        when(firebaseRepository.getSelectedRestaurant(any(RestaurantSelectedCallback.class))).thenReturn(selectedRestaurantLiveData);

        assertEquals(mockRestaurant, mapViewModel.getTodaySelectedRestaurant().getValue());
    }
    private List<PlacesItem> createPlacesItem() {
        PlacesItem placesItem = new PlacesItem();

        placesItem.setId("12345");

        DisplayName displayName = new DisplayName();
        displayName.setText("Mock Restaurant");
        placesItem.setDisplayName(displayName);


        placesItem.setFormattedAddress("123 Main St, City, Country");
        placesItem.setNationalPhoneNumber("123-456-7890");
        placesItem.setRating(4.5f);
        placesItem.setTakeout(true);
        placesItem.setWebsiteUri("https://example.com");

        List<String> types = new ArrayList<>();
        types.add("restaurant");
        types.add("food");
        placesItem.setTypes(types);

        List<PhotosItem> photos = new ArrayList<>();
        PhotosItem photosItem = new PhotosItem();
        photosItem.setName("photo1");
        photos.add(photosItem);
        placesItem.setPhotos(photos);

        Location location = new Location();
        location.setLatitude(40.7128);
        location.setLongitude(-74.0060);
        placesItem.setLocation(location);

        EditorialSummary editorialSummary = new EditorialSummary();
        editorialSummary.setText("This is a mock restaurant summary.");
        placesItem.setEditorialSummary(editorialSummary);

        placesItem.setDineIn(true);
        List<PlacesItem> placesItemList = new ArrayList<>();
        placesItemList.add(placesItem);
        return placesItemList;
    }
}
