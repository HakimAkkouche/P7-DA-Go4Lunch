package com.haksoftware.go4lunch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.haksoftware.go4lunch.databinding.ActivityMainBinding;
import com.haksoftware.go4lunch.model.Colleague;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.repository.FirebaseRepository;
import com.haksoftware.go4lunch.ui.map.MapViewModel;
import com.haksoftware.go4lunch.utils.NotificationScheduler;
import com.haksoftware.go4lunch.utils.NotificationService;
import com.haksoftware.go4lunch.utils.RestaurantSelectedClickedListener;
import com.haksoftware.go4lunch.utils.ViewModelFactory;

public class MainActivity extends AppCompatActivity implements RestaurantSelectedClickedListener {
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private MapViewModel mapViewModel;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Restaurant selectedRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(this.getApplication())).get(MapViewModel.class);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        drawerLayout = binding.mainLayout;
        navigationView = binding.navView;

        initNavigationView();
        loadProfile();

    }
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
    private void initNavigationView() {
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.restaurants_fragment, R.id.colleagues_fragment)
                .setOpenableLayout(drawerLayout)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(binding.navViewBottom, navController);

        mapViewModel.getTodaySelectedRestaurant().observe(MainActivity.this, restaurant -> selectedRestaurant = restaurant);

        navigationView.setNavigationItemSelectedListener(
                item -> {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    if (item.getItemId() == R.id.nav_item_your_lunch) {
                        if(selectedRestaurant != null) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("selectedRestaurant", selectedRestaurant);

                            navController.navigate(R.id.action_MapFragment_to_RestaurantDetailFragment, bundle);
                        } else {
                            Toast.makeText(getApplicationContext(), "No restaurant selected for today", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    } else if (item.getItemId() == R.id.nav_item_settings) {
                        navController.navigate(R.id.action_MapFragment_to_SettingsFragment);

                    } else if (item.getItemId() == R.id.nav_item_logout) {
                        FirebaseRepository.getInstance().signOut(getApplicationContext());
                        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(loginIntent);
                        finish();

                        return true;
                    }
                    return false;
                });
    }

    private void loadProfile() {
        String nameUser = mapViewModel.getCurrentUser().getDisplayName();
        String emailUser= mapViewModel.getCurrentUser().getEmail();
        Uri pictureUrlUser = mapViewModel.getCurrentUser().getPhotoUrl();

        TextView nameTextView = navigationView.getHeaderView(0).findViewById(R.id.name);
        TextView emailTextView = navigationView.getHeaderView(0).findViewById(R.id.email);
        nameTextView.setText(nameUser);
        emailTextView.setText(emailUser);
        setProfileImage(pictureUrlUser);
    }
    private void setProfileImage(Uri profileImage){
        ImageView pictureImageView = navigationView.getHeaderView(0).findViewById(R.id.imageView);
        Glide.with(pictureImageView.getRootView())
                .load(profileImage)
                .circleCrop()
                .into(pictureImageView);
    }
    @Override
    public void onRestaurantSelectedClick(Restaurant restaurant, boolean isSelected) {
        selectedRestaurant = restaurant;
        if(isSelected) {
            FirebaseRepository.getInstance().getColleagueData().addOnSuccessListener(documentSnapshot -> {
                Colleague user = documentSnapshot.toObject(Colleague.class);
                if (user != null) {
                    if (user.getWantsNotification()) {
                        startService(new Intent(MainActivity.this, NotificationService.class));
                        NotificationScheduler.scheduleNotification(this, restaurant);
                    }
                }
            });
        }
        else {
            NotificationScheduler.cancelNotification(this, restaurant);
        }
    }

}
