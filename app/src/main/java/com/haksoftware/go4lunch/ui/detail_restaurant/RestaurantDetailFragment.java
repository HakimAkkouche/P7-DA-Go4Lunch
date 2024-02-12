package com.haksoftware.go4lunch.ui.detail_restaurant;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.haksoftware.go4lunch.R;
import com.haksoftware.go4lunch.databinding.FragmentRestaurantDetailBinding;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.utils.PlacesApiHelper;
import com.haksoftware.go4lunch.utils.ViewModelFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RestaurantDetailFragment extends Fragment {
    private FragmentRestaurantDetailBinding binding;
    private Restaurant clickedRestaurant;
    private RestaurantDetailViewModel restaurantDetailViewModel;
    private boolean isLikedRestaurant;

    public RestaurantDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clickedRestaurant = getArguments().getParcelable("selectedRestaurant");
            // Now you have the selected restaurant object, use it as needed.
        }
        restaurantDetailViewModel =
                new ViewModelProvider(this, ViewModelFactory.getInstance(requireContext())).get(RestaurantDetailViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRestaurantDetailBinding.inflate(inflater, container, false);

        initDetailRestaurant();
        updateChosenButtonColor();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }


    private void initDetailRestaurant() {

        Call<ResponseBody> call = PlacesApiHelper.getRestaurantPhoto(clickedRestaurant.getUrlPicture(), 400, 400);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (getViewLifecycleOwner().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                    if (response.isSuccessful()) {
                        try {
                            byte[] imageBytes = response.body().bytes();

                            Glide.with(requireContext())
                                    .load(imageBytes).centerCrop()
                                    .into(binding.imageRestaurant);
                            // Faites quelque chose avec les données binaires de l'image (par exemple, enregistrez-les dans un fichier)
                        } catch (IOException e) {
                            Log.e("Erreur", "Onfailure" + call.request().url() + " " + call.request().body().toString());
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println("Erreur : " + response.code() + " - " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Erreur", "Onfailure" + call.request().url());
                t.printStackTrace();
            }
        });

        binding.isTodayChosenRestFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                restaurantDetailViewModel.getSelectedRestaurant().observe(getViewLifecycleOwner(), selectedRestaurant -> {
                    restaurantDetailViewModel.getLastSelectedRestaurantDate().observe(getViewLifecycleOwner(), lastSelectedRestaurantDate -> {
                        if (selectedRestaurant != null && selectedRestaurant.getRestaurantId().equals(clickedRestaurant.getRestaurantId()) &&
                                currentDate.equals(lastSelectedRestaurantDate)) {
                            restaurantDetailViewModel.updateSelectedRestaurant(null, null);
                        } else {
                            restaurantDetailViewModel.updateSelectedRestaurant(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), clickedRestaurant);
                        }
                        updateChosenButtonColor();
                    });
                });

            }
        });

        binding.nameTextview.setText(clickedRestaurant.getName());
        binding.addressTextview.setText(clickedRestaurant.getAddress());
        binding.callButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + clickedRestaurant.getPhoneNumber()));
            startActivity(intent);
        });

        binding.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restaurantDetailViewModel.getLikedRestaurant(clickedRestaurant.getRestaurantId()).observe(getViewLifecycleOwner(), documentSnapshot -> {
                    if (documentSnapshot != null) {
                        restaurantDetailViewModel.removeLikedRestaurant(clickedRestaurant);
                        isLikedRestaurant = false;
                    } else {
                        isLikedRestaurant = true;
                        restaurantDetailViewModel.addLikedRestaurant(clickedRestaurant);
                    }
                    updateLikeButtonColor();
                });
            }
        });
        binding.websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickedRestaurant.getUrlWebsite() != "") {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickedRestaurant.getUrlWebsite()));
                    startActivity(browserIntent);
                } else {
                    Toast.makeText(getContext(), "This restaurant does not have a website", Toast.LENGTH_SHORT).show();
                }
            }
        });
        restaurantDetailViewModel.getLikedRestaurant(clickedRestaurant.getRestaurantId()).observe(getViewLifecycleOwner(), documentSnapshot -> {
            if (documentSnapshot != null) {
                isLikedRestaurant = true;
            } else {
                isLikedRestaurant = false;
            }
            updateLikeButtonColor();
        });
    }

    private void updateChosenButtonColor() {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        restaurantDetailViewModel.getSelectedRestaurant().observe(getViewLifecycleOwner(), selectedRestaurant -> {
            restaurantDetailViewModel.getLastSelectedRestaurantDate().observe(getViewLifecycleOwner(), lastSelectedRestaurantDate -> {
                if (selectedRestaurant != null && selectedRestaurant.getRestaurantId().equals(clickedRestaurant.getRestaurantId()) &&
                        currentDate.equals(lastSelectedRestaurantDate)) {
                    binding.isTodayChosenRestFloatingBtn.setColorFilter(getResources().getColor(R.color.colorGreen));
                } else {
                    binding.isTodayChosenRestFloatingBtn.setColorFilter(getResources().getColor(R.color.grey_star));
                }
            });
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
}