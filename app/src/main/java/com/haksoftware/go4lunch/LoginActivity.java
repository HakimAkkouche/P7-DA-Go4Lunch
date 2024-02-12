package com.haksoftware.go4lunch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.haksoftware.go4lunch.databinding.ActivityLoginBinding;
import com.haksoftware.go4lunch.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

public class LoginActivity  extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private ActivityLoginBinding binding;

    private UserRepository userRepository = UserRepository.getInstance();

    /**
     * {@inheritDoc}
     * <p>
     * Perform initialization of all fragments.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if(userRepository.getCurrentUser() != null) {
            launchActivityConnected();
        }
        else {
            startSignInActivity();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void startSignInActivity(){

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Launch the activity
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.mipmap.logo)
                        .build(),
                RC_SIGN_IN);
    }
    // Show Snack Bar with a message
    private void showSnackBar( String message){
        Snackbar.make(this.getCurrentFocus(), message, Snackbar.LENGTH_SHORT).show();
    }
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            // SUCCESS
            if (resultCode == RESULT_OK) {
                userRepository.createColleague();
                launchActivityConnected();
            } else {
                // ERRORS
                if (response == null) {
                    Toast.makeText(this, getString(R.string.error_authentication_canceled), Toast.LENGTH_SHORT).show();
                } else if (response.getError()!= null) {
                    if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                        Toast.makeText(this, getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                    } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        Toast.makeText(this, getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    private void launchActivityConnected(){
        Toast.makeText(this, getString(R.string.connection_succeed), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
