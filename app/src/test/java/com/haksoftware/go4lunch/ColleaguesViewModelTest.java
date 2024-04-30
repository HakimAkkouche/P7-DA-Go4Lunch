package com.haksoftware.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.haksoftware.go4lunch.model.Colleague;
import com.haksoftware.go4lunch.repository.FirebaseRepository;
import com.haksoftware.go4lunch.ui.colleagues.ColleaguesViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class ColleaguesViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @InjectMocks
    FirebaseRepository repository;

    ColleaguesViewModel colleaguesViewModel;
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        colleaguesViewModel = mock(ColleaguesViewModel.class);
        repository = mock(FirebaseRepository.class);
    }

    @Test
    public void testGetColleagues() {
        MutableLiveData<List<Colleague>> mockColleagueList = new MutableLiveData<>();
        List<Colleague> colleagueList = new ArrayList<>();
        colleagueList.add(mock(Colleague.class));
        colleagueList.add(mock(Colleague.class));
        colleagueList.add(mock(Colleague.class));
        mockColleagueList.postValue(colleagueList);

        when(colleaguesViewModel.getColleagues()).thenReturn(mockColleagueList);
        assertEquals(mockColleagueList, colleaguesViewModel.getColleagues());
    }
}
