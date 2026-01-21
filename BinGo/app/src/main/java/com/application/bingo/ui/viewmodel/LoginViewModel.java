package com.application.bingo.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.application.bingo.R;
import com.application.bingo.model.User;
import com.application.bingo.repository.UserRepository;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

/**
 * ViewModel for LoginFragment.
 * Handles user authentication with an offline-first approach.
 */
public class LoginViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final MutableLiveData<LoginState> _loginState = new MutableLiveData<>();
    public final LiveData<LoginState> loginState = _loginState;

    public LoginViewModel(@NonNull Application application) {
        this(application, new UserRepository(application));
    }

    /**
     * Constructor for testing purposes to allow repository injection.
     */
    public LoginViewModel(@NonNull Application application, UserRepository repository) {
        super(application);
        this.repository = repository;
    }

    /**
     * OFFLINE FIRST Strategy:
     * 1. Check local database (Room). If user exists, log in immediately.
     * 2. In parallel (or if not found locally), attempt validation via Firebase.
     *
     * @param email    User email
     * @param password User password
     */
    public void login(String email, String password) {
        _loginState.setValue(new LoginState.Loading());

        // 1. SEARCH LOCALLY FIRST (Offline First)
        repository.findLocalUser(email, password, localUser -> {
            if (localUser != null) {
                // FOUND! Login successful immediately
                _loginState.postValue(new LoginState.Success(
                        localUser.getName(), localUser.getSurname(), localUser.getAddress(), email, password));
                
                // If network is available, perform a silent sign-in on Firebase to refresh tokens
                if (repository.isInternetAvailable()) {
                    repository.firebaseSignIn(email, password);
                }
            } else {
                // 2. NOT FOUND LOCALLY -> ATTEMPT FIREBASE LOGIN
                if (repository.isInternetAvailable()) {
                    performFirebaseLogin(email, password);
                } else {
                    _loginState.postValue(new LoginState.Error("User not found locally and no internet connection."));
                }
            }
        });
    }

    /**
     * Attempts to sign in using Firebase and saves user data locally upon success.
     */
    private void performFirebaseLogin(String email, String password) {
        repository.firebaseSignIn(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser fbUser = task.getResult().getUser();
                String name = (fbUser != null && fbUser.getDisplayName() != null) ? fbUser.getDisplayName() : "";

                // Online success: save locally for future offline access
                User newUser = new User(name, "", "", email, password);
                repository.saveLocalUser(newUser, () -> 
                    _loginState.postValue(new LoginState.Success(name, "", "", email, password))
                );
            } else {
                handleFirebaseError(task.getException(), email, password);
            }
        });
    }

    /**
     * Handles specific Firebase authentication errors.
     */
    private void handleFirebaseError(Exception e, String email, String password) {
        if (e instanceof FirebaseAuthException) {
            String code = ((FirebaseAuthException) e).getErrorCode();
            
            // CREDENTIAL ERRORS -> Do NOT allow local login fallback
            if (code.equals("ERROR_INVALID_CREDENTIAL") || 
                code.equals("ERROR_USER_NOT_FOUND") || 
                code.equals("ERROR_WRONG_PASSWORD") || 
                code.equals("ERROR_USER_DISABLED") || 
                code.equals("ERROR_INVALID_EMAIL")) {
                _loginState.postValue(new LoginState.Error("Invalid credentials"));
                return;
            }
            
            // NETWORK ERROR -> Attempt local login fallback
            if (code.equals("ERROR_NETWORK_REQUEST_FAILED")) {
                repository.findLocalUser(email, password, localUser -> {
                    if (localUser != null) {
                        _loginState.postValue(new LoginState.Success(
                            localUser.getName(), localUser.getSurname(), localUser.getAddress(), email, password));
                    } else {
                        _loginState.postValue(new LoginState.Error("Invalid credentials"));
                    }
                });
                return;
            }
        }
        
        // FALLBACK: Handle any other unexpected errors
        _loginState.postValue(new LoginState.Error("Authentication error"));
    }

    /**
     * Handles sign-in using external credentials (e.g., Google).
     */
    public void loginWithGoogle(AuthCredential credential) {
        _loginState.setValue(new LoginState.Loading());
        repository.firebaseSignInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser fbUser = task.getResult().getUser();
                String name = fbUser != null && fbUser.getDisplayName() != null ? fbUser.getDisplayName() : "";
                String email = fbUser != null ? fbUser.getEmail() : "";

                // Save Google user details locally
                repository.saveLocalUser(new User(name, "", "", email, ""), () ->
                    _loginState.postValue(new LoginState.Success(name, "", "", email, ""))
                );
            } else {
                _loginState.postValue(new LoginState.Error(getApplication().getString(R.string.error_google_login)));
            }
        });
    }

    /**
     * Represents the various states of the Login process.
     */
    public static abstract class LoginState {
        public static class Loading extends LoginState {}
        
        public static class Success extends LoginState {
            public final String name, surname, address, email, password;
            public Success(String name, String surname, String address, String email, String password) {
                this.name = name; 
                this.surname = surname; 
                this.address = address; 
                this.email = email; 
                this.password = password;
            }
        }

        public static class Error extends LoginState {
            public final String message;
            public Error(String message) { this.message = message; }
        }
    }
}
