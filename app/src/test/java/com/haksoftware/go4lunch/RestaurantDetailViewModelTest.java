package com.haksoftware.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.haksoftware.go4lunch.model.Colleague;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.repository.FirebaseRepository;
import com.haksoftware.go4lunch.ui.detail_restaurant.LikedRestaurantCallback;
import com.haksoftware.go4lunch.ui.detail_restaurant.RestaurantDetailViewModel;
import com.haksoftware.go4lunch.utils.RestaurantSelectedCallback;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RestaurantDetailViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @InjectMocks
    FirebaseRepository repository;
    RestaurantDetailViewModel restaurantDetailViewModel;
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = mock(FirebaseRepository.class);
        restaurantDetailViewModel = new RestaurantDetailViewModel(repository);
    }

    @Test
    public void testGetSelectedRestaurant() {
        // Création d'un objet Restaurant simulé pour le test
        Restaurant mockRestaurant = new Restaurant();

        // Définition du comportement de la méthode getSelectedRestaurant du repository
        doAnswer(invocation -> {
            RestaurantSelectedCallback callback = invocation.getArgument(0);
            // Appel du callback avec le restaurant simulé
            callback.onRestaurantSelected(mockRestaurant);
            return null; // La méthode retourne void, donc nous renvoyons null ici
        }).when(repository).getSelectedRestaurant(any(RestaurantSelectedCallback.class));

        // Appel de la méthode à tester
        MutableLiveData<Restaurant> resultLiveData = restaurantDetailViewModel.getSelectedRestaurant();

        // Vérification que le LiveData retourné n'est pas null
        assertNotNull(resultLiveData);

        // Observer le LiveData pour vérifier son contenu
        Observer<Restaurant> observer = restaurant -> {
            // Vérifier que le restaurant retourné par le LiveData correspond au restaurant simulé
            assertEquals(mockRestaurant, restaurant);
        };

        resultLiveData.observeForever(observer);
    }


    @Test
    public void testGetRestaurantColleagues() throws InterruptedException {
        // Créer une liste simulée de collègues
        List<Colleague> colleagueList = new ArrayList<>();
        colleagueList.add(mock(Colleague.class));
        colleagueList.add(mock(Colleague.class));

        Restaurant restaurant = new Restaurant("id",
                "name",
                null,2,null,null,"address","hours", "editorial","website",2.3,2.3);

        // Utiliser CountDownLatch pour attendre la fin de la requête Firebase
        CountDownLatch latch = new CountDownLatch(1);

        // Configurer le mock pour retourner la LiveData simulée
        MutableLiveData<List<Colleague>> mockColleagueList = new MutableLiveData<>();
        mockColleagueList.setValue(colleagueList);
        when(repository.getColleaguesByRestaurant(anyString())).thenReturn(mockColleagueList);

        // Appeler la méthode à tester
        LiveData<List<Colleague>> resultLiveData = restaurantDetailViewModel.getRestaurantColleagues(restaurant);

        // Observer le LiveData pour savoir quand il est mis à jour
        resultLiveData.observeForever(colleagues -> {
            // Vérifier le contenu du LiveData
            assertEquals(colleagueList, colleagues);

            // Compter le latch
            latch.countDown();
        });

        // Attendre que le latch soit à zéro
        latch.await();

    }
    @Test
    public void testGetLikedRestaurant() {
        // RestaurantId de test
        String restaurantId = "testRestaurantId";
        // Résultat de la méthode à mocker (true pour cet exemple)
        boolean expectedResult = true;

        // Mock de la méthode getLikedRestaurant du repository
        doAnswer(invocation -> {
            LikedRestaurantCallback callback = invocation.getArgument(1);
            // Appel du callback avec le résultat attendu
            callback.onLikedRestaurantReceived(expectedResult);
            return null; // La méthode retourne void, donc nous renvoyons null ici
        }).when(repository).getLikedRestaurant(eq(restaurantId), any(LikedRestaurantCallback.class));

        // Appel de la méthode à tester
        MutableLiveData<Boolean> resultLiveData = restaurantDetailViewModel.getLikedRestaurant(restaurantId);

        // Vérification que le LiveData retourné n'est pas null
        assertNotNull(resultLiveData);

        // Observer le LiveData pour vérifier son contenu
        Observer<Boolean> observer = isLiked -> {
            // Vérifier le contenu du LiveData avec le résultat attendu
            assertEquals(expectedResult, isLiked);
        };

        resultLiveData.observeForever(observer);
    }

}
