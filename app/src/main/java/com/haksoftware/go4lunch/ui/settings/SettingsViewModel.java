package com.haksoftware.go4lunch.ui.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.haksoftware.go4lunch.repository.UserRepository;

public class SettingsViewModel extends ViewModel {
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private final UserRepository userRepository;


    public SettingsViewModel(Context context, UserRepository userRepository) {
        this.context = context;
        this.userRepository = userRepository;
    }

    public void uploadImageProfile(Uri profileImageUri) {
        userRepository.uploadImage(profileImageUri).addOnSuccessListener(taskSnapshot ->
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
            userRepository.setNewUrlPicture(profileImageUri);
            Toast.makeText(context, "Image profile uploaded.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(exception -> {
            Toast.makeText(context, "Error while uploading image", Toast.LENGTH_SHORT).show();
            Log.e("ERROR", "Error upload image "+ exception.getMessage());
        } ));
    }

    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public MutableLiveData<Boolean> getWantsNotification() {
        return userRepository.getWantsNotification();
    }
    public void setWantsNotification(boolean wantsNotification) {
        userRepository.setWantsNotification(wantsNotification);
    }
}
