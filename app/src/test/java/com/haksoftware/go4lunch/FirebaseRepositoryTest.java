package com.haksoftware.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.haksoftware.go4lunch.model.Colleague;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.repository.FirebaseRepository;
import com.haksoftware.go4lunch.ui.restaurant_list.ColleagueRestaurantCallback;
import com.haksoftware.go4lunch.utils.RestaurantSelectedCallback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

public class FirebaseRepositoryTest {
    @InjectMocks
    FirebaseRepository repository;
    @Mock
    ColleagueRestaurantCallback mockColleagueCallback;
    @Mock
    RestaurantSelectedCallback mockSelectedRestaurantCallback;
    @Mock
    Restaurant mockRestaurant;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = mock(FirebaseRepository.class);
    }
    @Test
    public void testGetCurrentUserReturnsFirebaseUser() {
        FirebaseUser user = mock(FirebaseUser.class);
        when(repository.getCurrentUser()).thenReturn(user);

        FirebaseUser result = repository.getCurrentUser();

        assertEquals(user, result);
    }

    @Test
    public void testGetCurrentUserReturnsNull() {
        when(repository.getCurrentUser()).thenReturn(null);

        FirebaseUser result = repository.getCurrentUser();

        assertNull(result);
    }
    @Test
    public void testGetAllColleagues() {
        MutableLiveData<List<Colleague>> colleagueList = new MutableLiveData<>();
        when(repository.getAllColleagues()).thenReturn(colleagueList);

        assertEquals(colleagueList, repository.getAllColleagues());
    }
    @Test
    public void testGetAllColleaguesByRestaurant() {
        MutableLiveData<List<Colleague>> colleagueList = new MutableLiveData<>();
        Restaurant restaurant = mock(Restaurant.class);
        when(repository.getColleaguesByRestaurant(restaurant.getRestaurantId())).thenReturn(colleagueList);

        assertEquals(colleagueList, repository.getColleaguesByRestaurant(restaurant.getRestaurantId()));
    }
    @Test
    public void testGetColleaguesCount() {
        Mockito.doAnswer(invocation -> {
            ColleagueRestaurantCallback callback = invocation.getArgument(1);
            callback.onColleaguesReceived(5);
            return null;
        }).when(repository).getColleaguesCount(Mockito.any(Restaurant.class), Mockito.any(ColleagueRestaurantCallback.class));

        repository.getColleaguesCount(mockRestaurant, mockColleagueCallback);

        Mockito.verify(mockColleagueCallback).onColleaguesReceived(5);
    }
    @Test
    public void testGetSelectedRestaurant() {

        Restaurant mockRestaurant = mock(Restaurant.class);
        Mockito.doAnswer(invocation -> {
            RestaurantSelectedCallback callback = invocation.getArgument(0);
            callback.onRestaurantSelected(mockRestaurant);
            return null;
        }).when(repository).getSelectedRestaurant(Mockito.any(RestaurantSelectedCallback.class));

        repository.getSelectedRestaurant(mockSelectedRestaurantCallback);
        Mockito.verify(mockSelectedRestaurantCallback).onRestaurantSelected(mockRestaurant);
    }
    @Test
    public void testGetWantsNotification() {
        MutableLiveData<Boolean> wantsNotification = new MutableLiveData<>();
        when(repository.getWantsNotification()).thenReturn(wantsNotification);

        assertEquals(wantsNotification, repository.getWantsNotification());
    }
}