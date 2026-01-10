package com.application.bingo.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.application.bingo.model.User;
import com.application.bingo.repository.UserRepository;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final MutableLiveData<LoginState> _loginState = new MutableLiveData<>();
    public final LiveData<LoginState> loginState = _loginState;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    public void login(String email, String password) {
        _loginState.setValue(new LoginState.Loading());

        repository.findLocalUser(email, password, localUser -> {
            if (localUser != null) {
                _loginState.postValue(new LoginState.Success(localUser.getName(), localUser.getSurname(), localUser.getAddress(), email, password));
            } else {
                repository.firebaseSignIn(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser fbUser = task.getResult().getUser();
                        String name = fbUser != null && fbUser.getDisplayName() != null ? fbUser.getDisplayName() : "";
                        User newUser = new User(name, "", "", email, password);
                        repository.saveLocalUser(newUser, () -> 
                            _loginState.postValue(new LoginState.Success(name, "", "", email, password))
                        );
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Errore autenticazione";
                        _loginState.postValue(new LoginState.Error(error));
                    }
                });
            }
        });
    }

    public void loginWithGoogle(AuthCredential credential) {
        _loginState.setValue(new LoginState.Loading());
        repository.firebaseSignInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser fbUser = task.getResult().getUser();
                String name = fbUser != null && fbUser.getDisplayName() != null ? fbUser.getDisplayName() : "";
                String email = fbUser != null ? fbUser.getEmail() : "";
                
                repository.saveLocalUser(new User(name, "", "", email, ""), () -> 
                    _loginState.postValue(new LoginState.Success(name, "", "", email, ""))
                );
            } else {
                _loginState.postValue(new LoginState.Error("Google Login fallito"));
            }
        });
    }

    public static abstract class LoginState {
        public static class Loading extends LoginState {}
        public static class Success extends LoginState {
            public final String name, surname, address, email, password;
            public Success(String name, String surname, String address, String email, String password) {
                this.name = name; this.surname = surname; this.address = address; this.email = email; this.password = password;
            }
        }
        public static class Error extends LoginState {
            public final String message;
            public Error(String message) { this.message = message; }
        }
    }
}