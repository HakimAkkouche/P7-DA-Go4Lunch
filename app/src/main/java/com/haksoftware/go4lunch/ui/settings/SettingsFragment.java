package com.haksoftware.go4lunch.ui.settings;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.haksoftware.go4lunch.R;
import com.haksoftware.go4lunch.databinding.FragmentSettingsBinding;
import com.haksoftware.go4lunch.utils.OnProfileUpdateListener;
import com.haksoftware.go4lunch.utils.ViewModelFactory;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements OnWantsNotificationListener {
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private SettingsViewModel settingsViewModel;
    private ImageView profileImageView;
    private SwitchCompat notificationSwitch;
    private final ActivityResultLauncher<String> choosePhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    Glide.with(this)
                            .load(uri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profileImageView);
                    settingsViewModel.uploadImageProfile(uri);
                    if (getActivity() instanceof OnProfileUpdateListener) {
                        OnProfileUpdateListener listener = (OnProfileUpdateListener) getActivity();
                        listener.updateProfilePhoto(uri);
                    }
                } else {
                    Toast.makeText(requireContext(), "No image chosen", Toast.LENGTH_SHORT).show();
                }
            }
    );

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().getApplication())).get(SettingsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSettingsBinding binding = FragmentSettingsBinding.inflate(inflater, container, false);


        profileImageView = binding.profilePicture;
        notificationSwitch = binding.notificationSwitch;

        Uri pictureUrlUser = settingsViewModel.getCurrentUser().getPhotoUrl();
        Glide.with(profileImageView.getRootView())
                .load(pictureUrlUser)
                .circleCrop()
                .into(profileImageView);
        profileImageView.setOnClickListener(view -> addFile());

        settingsViewModel.getWantsNotification().observe(getViewLifecycleOwner(), this::updateWantsNotification);
        notificationSwitch.setOnClickListener(view -> {
            SwitchCompat switchCompat = (SwitchCompat) view;
            settingsViewModel.setWantsNotification(switchCompat.isChecked());
        });

        return binding.getRoot();
    }

    @AfterPermissionGranted(RC_IMAGE_PERMS)
    private void addFile(){
        if (!EasyPermissions.hasPermissions(requireContext(), PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        choosePhotoLauncher.launch("image/*");
    }
    @Override
    public void updateWantsNotification(boolean wantsNotification) {
        notificationSwitch.setChecked(wantsNotification);
    }

}