package com.haksoftware.go4lunch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
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
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.haksoftware.go4lunch.databinding.ActivityMainBinding;
import com.haksoftware.go4lunch.repository.UserRepository;
import com.haksoftware.go4lunch.ui.map.MapFragment;
import com.haksoftware.go4lunch.ui.map.MapViewModel;
import com.haksoftware.go4lunch.utils.ViewModelFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final int RC_SIGN_IN = 123;
    private static final int YOUR_LUNCH_ID = R.id.nav_item_your_lunch;
    private static final int SETTINGS_ID = R.id.nav_item_settings;
    private static final int LOGOUT_ID = R.id.nav_item_logout;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    private ActivityMainBinding binding;
    private MapViewModel mapViewModel;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(MapViewModel.class);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        drawerLayout = binding.mainLayout;
        navigationView = binding.navView;

        initNavigationView();

        loadProfile();


    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Toast.makeText( getApplicationContext(),"MenuItem"+item.getItemId(), Toast.LENGTH_SHORT).show();
        if (item.getItemId() == R.id.nav_item_your_lunch) {

        } else if (item.getItemId() == R.id.nav_item_settings) {

        } else if (item.getItemId() == R.id.nav_item_logout) {
            mapViewModel.logOut(this);
            Toast.makeText(this, "logout click", Toast.LENGTH_SHORT).show();
            return true;
        } else if(item.getItemId() == R.id.navigation_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, new MapFragment()).commit();
        }
        return false;
    }
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void initNavigationView() {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .setOpenableLayout(drawerLayout)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(binding.navViewBottom, navController);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        if (item.getItemId() == R.id.nav_item_your_lunch) {
                            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (item.getItemId() == R.id.nav_item_settings) {
                            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (item.getItemId() == R.id.nav_item_logout) {
                            UserRepository.getInstance().signOut(getApplicationContext());
                            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(loginIntent);
                            finish();

                            return true;
                        }
                        return false;
                    }
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
        ImageView pictureImageView = navigationView.getHeaderView(0).findViewById(R.id.imageView);
        Glide.with(pictureImageView.getRootView())
                .load(pictureUrlUser)
                .circleCrop()
                .into(pictureImageView);
    }
}
