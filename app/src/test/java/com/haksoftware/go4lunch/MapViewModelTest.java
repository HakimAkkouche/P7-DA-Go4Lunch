package com.haksoftware.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.repository.FirebaseRepository;
import com.haksoftware.go4lunch.ui.map.ColleaguesCountLoadedCallback;
import com.haksoftware.go4lunch.ui.map.MapViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class MapViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @InjectMocks
    FirebaseRepository repository;
    MapViewModel mapViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mapViewModel = mock(MapViewModel.class);
        repository = mock(FirebaseRepository.class);
    }

    @Test
    public void testGetCurrentUser() {
        FirebaseUser user = mock(FirebaseUser.class);
        when(mapViewModel.getCurrentUser()).thenReturn(user);

        FirebaseUser result = mapViewModel.getCurrentUser();

        assertEquals(user, result);
    }
    @Test
    public void testGetNearbyRestaurants() {
        MutableLiveData<List<Restaurant>> nearByRestaurant = new MutableLiveData<>();
        List<Restaurant> restaurantList = new ArrayList<>();
        restaurantList.add(mock(Restaurant.class));
        restaurantList.add(mock(Restaurant.class));
        restaurantList.add(mock(Restaurant.class));
        nearByRestaurant.setValue(restaurantList);
        ColleaguesCountLoadedCallback callback = mock(ColleaguesCountLoadedCallback.class);
        when(mapViewModel.getNearbyRestaurants(48.85849006983193, 2.294438381937397, 500, callback)).thenReturn(nearByRestaurant);
        assertEquals(nearByRestaurant, mapViewModel.getNearbyRestaurants(48.85849006983193, 2.294438381937397, 500, callback));
    }

    @Test
    public void testGetRestaurantMutableLiveData() {
        //Given
        MutableLiveData<List<Restaurant>> mockRestaurantList = new MutableLiveData<>();

        List<Restaurant> restaurantList = new ArrayList<>();
        restaurantList.add(mock(Restaurant.class));
        restaurantList.add(mock(Restaurant.class));
        restaurantList.add(mock(Restaurant.class));
        mockRestaurantList.postValue(restaurantList);
        //when
        when(mapViewModel.getRestaurantMutableLiveData()).thenReturn(mockRestaurantList);
        //then
        assertEquals(mockRestaurantList, mapViewModel.getRestaurantMutableLiveData());
    }
    @Test
    public void testGetTodaySelectedRestaurant() {
        MutableLiveData<Restaurant> mockRestaurant = new MutableLiveData<>();
        mockRestaurant.postValue(mock(Restaurant.class));

        when(mapViewModel.getTodaySelectedRestaurant()).thenReturn(mockRestaurant);
        assertEquals(mockRestaurant, mapViewModel.getTodaySelectedRestaurant());
    }
}
