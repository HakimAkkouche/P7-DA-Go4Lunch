package com.haksoftware.go4lunch.ui.detail_restaurant;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haksoftware.go4lunch.R;
import com.haksoftware.go4lunch.databinding.FragmentRestaurantDetailBinding;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.utils.PlacesApiHelper;
import com.haksoftware.go4lunch.utils.RestaurantSelectedClickedListener;
import com.haksoftware.go4lunch.utils.ViewModelFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RestaurantDetailFragment extends Fragment implements LikedRestaurantCallback {
    private FragmentRestaurantDetailBinding binding;
    private Restaurant clickedRestaurant;
    private RestaurantDetailViewModel restaurantDetailViewModel;
    private boolean isLikedRestaurant;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clickedRestaurant = getArguments().getParcelable("selectedRestaurant");
        }
        restaurantDetailViewModel =
                new ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().getApplication())).get(RestaurantDetailViewModel.class);
        restaurantDetailViewModel.setLikedRestaurantCallback(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRestaurantDetailBinding.inflate(inflater, container, false);

        initDetailRestaurant();
        updateChosenButtonColor();
        RecyclerView recyclerView = binding.colleaguesRecyclerView;

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RestaurantParticipantsListAdapter restaurantParticipantsListAdapter = new RestaurantParticipantsListAdapter();
        recyclerView.setAdapter(restaurantParticipantsListAdapter);

        restaurantDetailViewModel.getRestaurantColleagues(clickedRestaurant).observe(getViewLifecycleOwner(),
                restaurantParticipantsListAdapter::submitList);
        return binding.getRoot();
    }

    private void initDetailRestaurant() {

        Call<ResponseBody> call = PlacesApiHelper.getRestaurantPhoto(clickedRestaurant.getUrlPicture(), 400, 400);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (getViewLifecycleOwner().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                    if (response.isSuccessful()) {
                        try {
                            if(response.body() != null) {
                                byte[] imageBytes = response.body().bytes();

                                Glide.with(requireContext())
                                        .load(imageBytes).centerCrop()
                                        .into(binding.imageRestaurant);
                            }
                        } catch (IOException e) {
                            Log.e("Erreur", "Onfailure" + call.request().url() + " " + Objects.requireNonNull(call.request().body()));
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println("Erreur : " + response.code() + " - " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("Erreur", "Onfailure" + call.request().url());
                t.printStackTrace();
            }
        });

        binding.nameTextview.setText(clickedRestaurant.getName());
        binding.addressTextview.setText(clickedRestaurant.getAddress());
        binding.callButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + clickedRestaurant.getPhoneNumber()));
            startActivity(intent);
        });

        binding.isTodayChosenRestFloatingBtn.setOnClickListener(view -> {

            RestaurantSelectedClickedListener listener = (RestaurantSelectedClickedListener) getActivity();
            restaurantDetailViewModel.getSelectedRestaurant().observe(getViewLifecycleOwner(), selectedRestaurant -> {
                if (selectedRestaurant != null && selectedRestaurant.getRestaurantId().equals(clickedRestaurant.getRestaurantId())) {
                    restaurantDetailViewModel.updateSelectedRestaurant(null, null);
                    if (listener != null) {
                        listener.onRestaurantSelectedClick(null, false);
                    }
                } else {
                    restaurantDetailViewModel.updateSelectedRestaurant(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), clickedRestaurant);
                    if (listener != null) {
                        listener.onRestaurantSelectedClick(clickedRestaurant, true);
                    }
                }
                updateChosenButtonColor();
            });

        });


        binding.likeButton.setOnClickListener(view -> restaurantDetailViewModel
                .getLikedRestaurant(clickedRestaurant.getRestaurantId()).observe(getViewLifecycleOwner(),
                        isLikedRestaurant -> {
            if(this.isLikedRestaurant) {
                restaurantDetailViewModel.removeLikedRestaurant(clickedRestaurant.getRestaurantId());
            }
            else {

                restaurantDetailViewModel.addLikedRestaurant(clickedRestaurant.getRestaurantId());
            }
            updateLikeButtonColor();
        }));
        binding.websiteButton.setOnClickListener(view -> {
            if (!Objects.equals(clickedRestaurant.getUrlWebsite(), "")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickedRestaurant.getUrlWebsite()));
                startActivity(browserIntent);
            } else {
                Toast.makeText(getContext(), "This restaurant does not have a website", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateChosenButtonColor() {
        restaurantDetailViewModel.getSelectedRestaurant().observe(getViewLifecycleOwner(), selectedRestaurant -> {
            if (selectedRestaurant != null && selectedRestaurant.getRestaurantId().equals(clickedRestaurant.getRestaurantId())) {
                binding.isTodayChosenRestFloatingBtn.setColorFilter(getResources().getColor(R.color.colorGreen));
            } else {
                binding.isTodayChosenRestFloatingBtn.setColorFilter(getResources().getColor(R.color.grey_star));
            }
        });
    }
    private void updateLikeButtonColor() {
            if(isLikedRestaurant) {
                binding.likedImgview.setColorFilter(getResources().getColor(R.color.gold_star));
            }
            else {
                binding.likedImgview.setColorFilter(getResources().getColor(R.color.grey_star));
            }
    }

    @Override
    public void onLikedRestaurantReceived(boolean isLiked) {
        this.isLikedRestaurant = !isLiked;
        updateLikeButtonColor();
    }
}