package com.haksoftware.go4lunch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.haksoftware.go4lunch.databinding.ActivityMainBinding;
import com.haksoftware.go4lunch.ui.map.MapViewModel;
import com.haksoftware.go4lunch.utils.ViewModelFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ActivityMainBinding binding;
    private MapViewModel mapViewModel;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navigationView = binding.navView;

        mapViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(getApplication())).get(MapViewModel.class);


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.restaurants_fragment, R.id.colleagues_fragment)
                .build();
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        drawerLayout = binding.mainLayout;
        NavigationUI.setupWithNavController(binding.navViewBottom, navController);

        binding.navView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        loadProfile();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
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
}
