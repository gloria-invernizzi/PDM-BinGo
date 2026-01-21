package com.application.bingo.ui.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.application.bingo.model.User;
import com.application.bingo.repository.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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

    @Mock
    private Task<AuthResult> authResultTask;

    @Mock
    private AuthResult authResult;

    @Mock
    private FirebaseUser firebaseUser;

    private LoginViewModel viewModel;

    @Before
    public void setUp() {
        viewModel = new LoginViewModel(application, userRepository);
    }

    @Test
    public void login_emitsLoadingState() {
        viewModel.login("test@test.com", "password");
        assertTrue(viewModel.loginState.getValue() instanceof LoginViewModel.LoginState.Loading);
    }

    @Test
    public void login_successLocal() {
        String email = "test@test.com";
        String password = "password";
        User user = new User("John", "Doe", "Address", email, password);

        doAnswer(invocation -> {
            UserRepository.RepositoryCallback<User> callback = invocation.getArgument(2);
            callback.onComplete(user);
            return null;
        }).when(userRepository).findLocalUser(eq(email), eq(password), any());

        viewModel.login(email, password);

        assertTrue(viewModel.loginState.getValue() instanceof LoginViewModel.LoginState.Success);
        LoginViewModel.LoginState.Success success = (LoginViewModel.LoginState.Success) viewModel.loginState.getValue();
        assertEquals("John", success.name);
    }

    @Test
    public void login_failedLocalNoInternet() {
        String email = "test@test.com";
        String password = "password";

        doAnswer(invocation -> {
            UserRepository.RepositoryCallback<User> callback = invocation.getArgument(2);
            callback.onComplete(null);
            return null;
        }).when(userRepository).findLocalUser(eq(email), eq(password), any());

        when(userRepository.isInternetAvailable()).thenReturn(false);

        viewModel.login(email, password);

        assertTrue(viewModel.loginState.getValue() instanceof LoginViewModel.LoginState.Error);
        assertEquals("User not found locally and no internet connection.", 
            ((LoginViewModel.LoginState.Error) viewModel.loginState.getValue()).message);
    }

    @Test
    public void login_failedLocalSuccessFirebase() {
        String email = "test@test.com";
        String password = "password";

        doAnswer(invocation -> {
            ((UserRepository.RepositoryCallback<User>) invocation.getArgument(2)).onComplete(null);
            return null;
        }).when(userRepository).findLocalUser(eq(email), eq(password), any());

        when(userRepository.isInternetAvailable()).thenReturn(true);
        when(userRepository.firebaseSignIn(email, password)).thenReturn(authResultTask);
        when(authResultTask.isSuccessful()).thenReturn(true);
        when(authResultTask.getResult()).thenReturn(authResult);
        when(authResult.getUser()).thenReturn(firebaseUser);
        when(firebaseUser.getDisplayName()).thenReturn("FirebaseUser");

        ArgumentCaptor<OnCompleteListener<AuthResult>> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        viewModel.login(email, password);
        verify(authResultTask).addOnCompleteListener(listenerCaptor.capture());
        listenerCaptor.getValue().onComplete(authResultTask);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(userRepository).saveLocalUser(any(User.class), runnableCaptor.capture());
        runnableCaptor.getValue().run();

        assertTrue(viewModel.loginState.getValue() instanceof LoginViewModel.LoginState.Success);
        assertEquals("FirebaseUser", ((LoginViewModel.LoginState.Success) viewModel.loginState.getValue()).name);
    }

    @Test
    public void login_firebaseError_InvalidCredentials() {
        String email = "test@test.com";
        String password = "password";

        doAnswer(invocation -> {
            ((UserRepository.RepositoryCallback<User>) invocation.getArgument(2)).onComplete(null);
            return null;
        }).when(userRepository).findLocalUser(eq(email), eq(password), any());

        when(userRepository.isInternetAvailable()).thenReturn(true);
        when(userRepository.firebaseSignIn(email, password)).thenReturn(authResultTask);
        when(authResultTask.isSuccessful()).thenReturn(false);
        
        FirebaseAuthException exception = mock(FirebaseAuthException.class);
        when(exception.getErrorCode()).thenReturn("ERROR_WRONG_PASSWORD");
        when(authResultTask.getException()).thenReturn(exception);

        ArgumentCaptor<OnCompleteListener<AuthResult>> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        viewModel.login(email, password);
        verify(authResultTask).addOnCompleteListener(listenerCaptor.capture());
        listenerCaptor.getValue().onComplete(authResultTask);

        assertTrue(viewModel.loginState.getValue() instanceof LoginViewModel.LoginState.Error);
        assertEquals("Invalid credentials", ((LoginViewModel.LoginState.Error) viewModel.loginState.getValue()).message);
    }

    @Test
    public void login_firebaseNetworkError_FallbackLocalSuccess() {
        String email = "test@test.com";
        String password = "password";
        User localUser = new User("Local", "User", "", email, password);

        // First local check fails (common scenario before attempting Firebase)
        doAnswer(invocation -> {
            ((UserRepository.RepositoryCallback<User>) invocation.getArgument(2)).onComplete(null);
            return null;
        }).when(userRepository).findLocalUser(eq(email), eq(password), any());

        when(userRepository.isInternetAvailable()).thenReturn(true);
        when(userRepository.firebaseSignIn(email, password)).thenReturn(authResultTask);
        when(authResultTask.isSuccessful()).thenReturn(false);

        FirebaseAuthException exception = mock(FirebaseAuthException.class);
        when(exception.getErrorCode()).thenReturn("ERROR_NETWORK_REQUEST_FAILED");
        when(authResultTask.getException()).thenReturn(exception);

        ArgumentCaptor<OnCompleteListener<AuthResult>> listenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        viewModel.login(email, password);
        verify(authResultTask).addOnCompleteListener(listenerCaptor.capture());

        // Now mock the SECOND local check (the fallback) to succeed
        doAnswer(invocation -> {
            ((UserRepository.RepositoryCallback<User>) invocation.getArgument(2)).onComplete(localUser);
            return null;
        }).when(userRepository).findLocalUser(eq(email), eq(password), any());

        listenerCaptor.getValue().onComplete(authResultTask);

        assertTrue(viewModel.loginState.getValue() instanceof LoginViewModel.LoginState.Success);
        assertEquals("Local", ((LoginViewModel.LoginState.Success) viewModel.loginState.getValue()).name);
    }
}
