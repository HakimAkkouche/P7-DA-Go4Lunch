package com.haksoftware.go4lunch.ui.settings;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.haksoftware.go4lunch.repository.FirebaseRepository;

public class SettingsViewModel extends AndroidViewModel {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final FirebaseRepository firebaseRepository;


    public SettingsViewModel(Application application, FirebaseRepository firebaseRepository) {
        super(application);
        this.context = getApplication().getBaseContext();
        this.firebaseRepository = firebaseRepository;
    }

    public void uploadImageProfile(Uri profileImageUri) {
        firebaseRepository.uploadImage(profileImageUri).addOnSuccessListener(taskSnapshot ->
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
            firebaseRepository.setNewUrlPicture(profileImageUri);
            Toast.makeText(context, "Image profile uploaded.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(exception -> {
            Toast.makeText(context, "Error while uploading image", Toast.LENGTH_SHORT).show();
            Log.e("ERROR", "Error upload image "+ exception.getMessage());
        } ));
    }

    public FirebaseUser getCurrentUser() {
        return firebaseRepository.getCurrentUser();
    }

    public MutableLiveData<Boolean> getWantsNotification() {
        return firebaseRepository.getWantsNotification();
    }
    public void setWantsNotification(boolean wantsNotification) {
        firebaseRepository.setWantsNotification(wantsNotification);
    }
}
