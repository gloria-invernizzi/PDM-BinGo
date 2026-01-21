package com.application.bingo.ui.viewmodel;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.application.bingo.repository.UserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests for LoginViewModel.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginViewModelTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Mock
    private UserRepository userRepository;

    @Mock
    private Application application;

    private LoginViewModel viewModel;

    @Before
    public void setUp() {
        // Initialize the ViewModel with mocked dependencies
        viewModel = new LoginViewModel(application, userRepository);
    }

    @Test
    public void login_emitsLoadingState() {
        // Trigger login
        viewModel.login("test@test.com", "password");
        
        // Verify that the initial state is Loading
        assertTrue(viewModel.loginState.getValue() instanceof LoginViewModel.LoginState.Loading);
    }
}
