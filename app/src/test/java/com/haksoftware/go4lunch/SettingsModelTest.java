package com.haksoftware.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.haksoftware.go4lunch.repository.FirebaseRepository;
import com.haksoftware.go4lunch.ui.settings.SettingsViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class SettingsModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @InjectMocks
    FirebaseRepository repository;

    SettingsViewModel settingsViewModel;
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = mock(FirebaseRepository.class);
        settingsViewModel = new SettingsViewModel(mock(Application.class), repository);
    }

    @Test
    public void testGetCurrentUser() {
        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(repository.getCurrentUser()).thenReturn(mockUser);
        assertEquals(mockUser, settingsViewModel.getCurrentUser());
    }
    @Test
    public void testGetWantsNotification() {
        MutableLiveData<Boolean> mockWantsNotification = new MutableLiveData<>();
        mockWantsNotification.postValue(true);

        when(repository.getWantsNotification()).thenReturn(mockWantsNotification);
        assertEquals(mockWantsNotification, settingsViewModel.getWantsNotification());
    }
}
