package com.haksoftware.go4lunch;

import static org.mockito.Mockito.verify;

import androidx.annotation.NonNull;

import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.ResponseDistanceMatrix;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.ResponseGMAP;
import com.haksoftware.go4lunch.repository.GmapRepository;
import com.haksoftware.go4lunch.ui.restaurant_list.DistanceMatrixCallback;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GmapRepositoryTest {
    private MockWebServer server;

    @Mock
    private DistanceMatrixCallback callback;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        server = new MockWebServer();
        server.start();
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void testGetNearbyRestaurants() {
        GmapRepository repository = GmapRepository.getInstance();
        String mockResponse = "{\"status\":\"OK\"}";
        server.enqueue(new MockResponse().setBody(mockResponse));

        Call<ResponseGMAP> call = repository.getNearbyRestaurants( 37.7749, -122.4194, 1000);
        call.enqueue(new Callback<ResponseGMAP>() {
            @Override
            public void onResponse(@NonNull Call<ResponseGMAP> call, @NonNull Response<ResponseGMAP> response) {
                assert response.isSuccessful();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseGMAP> call, @NonNull Throwable t) {
                assert false;
            }
        });
    }

    @Test
    public void testGetDistance() {
        GmapRepository repository = GmapRepository.getInstance();
        String mockResponse = "{\"rows\":[{\"elements\":[{\"distance\":{\"text\":\"10 km\"}}]}]}";
        server.enqueue(new MockResponse().setBody(mockResponse));

        Call<ResponseDistanceMatrix> call = repository.getDistance("destination", "origin", "unit", callback);
        call.enqueue(new Callback<ResponseDistanceMatrix>() {
            @Override
            public void onResponse(@NonNull Call<ResponseDistanceMatrix> call, @NonNull Response<ResponseDistanceMatrix> response) {
                assert response.isSuccessful();
                verify(callback).onDistanceReceived("10 km");
            }

            @Override
            public void onFailure(@NonNull Call<ResponseDistanceMatrix> call, @NonNull Throwable t) {
                assert false;
            }
        });
    }
}
