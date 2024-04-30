package com.haksoftware.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.haksoftware.go4lunch.model.Colleague;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.repository.FirebaseRepository;
import com.haksoftware.go4lunch.ui.detail_restaurant.RestaurantDetailViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @InjectMocks
    FirebaseRepository repository;

    RestaurantDetailViewModel restaurantDetailViewModel;
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        restaurantDetailViewModel = mock(RestaurantDetailViewModel.class);
        repository = mock(FirebaseRepository.class);
    }

    @Test
    public void testGetSelectedRestaurant() {
        MutableLiveData<Restaurant> mockRestaurant = new MutableLiveData<>();
        mockRestaurant.postValue(mock(Restaurant.class));

        when(restaurantDetailViewModel.getSelectedRestaurant()).thenReturn(mockRestaurant);
        assertEquals(mockRestaurant, restaurantDetailViewModel.getSelectedRestaurant());
    }

    @Test
    public void testGetRestaurantColleagues(){
        MutableLiveData<List<Colleague>> mockCurrentLocation = new MutableLiveData<>();
        List<Colleague> colleagueList = new ArrayList<>();
        colleagueList.add(mock(Colleague.class));
        colleagueList.add(mock(Colleague.class));
        colleagueList.add(mock(Colleague.class));
        mockCurrentLocation.setValue(colleagueList);
        Restaurant mockRestaurant = mock(Restaurant.class);

        when(restaurantDetailViewModel.getRestaurantColleagues(mockRestaurant)).thenReturn(mockCurrentLocation);
        assertEquals(mockCurrentLocation, restaurantDetailViewModel.getRestaurantColleagues(mockRestaurant));
    }
    @Test
    public void testGetLikedRestaurant(){
        MutableLiveData<Boolean> mockIsDistanceLoaded = new MutableLiveData<>();
        mockIsDistanceLoaded.setValue(true);

        when(restaurantDetailViewModel.getLikedRestaurant("1")).thenReturn(mockIsDistanceLoaded);
        assertEquals(mockIsDistanceLoaded, restaurantDetailViewModel.getLikedRestaurant("1"));
    }
}
