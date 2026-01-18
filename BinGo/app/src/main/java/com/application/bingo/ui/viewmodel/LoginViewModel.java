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

public class LoginViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final MutableLiveData<LoginState> _loginState = new MutableLiveData<>();
    public final LiveData<LoginState> loginState = _loginState;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    /**
     * Strategia OFFLINE FIRST:
     * 1. Controlla il database locale (Room). Se l'utente esiste, entra subito.
     * 2. In parallelo (o se non trovato locale), prova la validazione su Firebase.
     */
    public void login(String email, String password) {
        _loginState.setValue(new LoginState.Loading());

        // 1. CERCA PRIMA IN LOCALE (Offline First)
        repository.findLocalUser(email, password, localUser -> {
            if (localUser != null) {
                // TROVATO! Entriamo subito
                _loginState.postValue(new LoginState.Success(
                        localUser.getName(), localUser.getSurname(), localUser.getAddress(), email, password));
                
                // Se c'è rete, facciamo un sign-in silente su Firebase per aggiornare il token
                if (repository.isInternetAvailable()) {
                    repository.firebaseSignIn(email, password);
                }
            } else {
                // 2. NON TROVATO LOCALE -> PROVA FIREBASE
                if (repository.isInternetAvailable()) {
                    performFirebaseLogin(email, password);
                } else {
                    _loginState.postValue(new LoginState.Error("Utente non trovato localmente e connessione assente."));
                }
            }
        });
    }

    private void performFirebaseLogin(String email, String password) {
        repository.firebaseSignIn(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser fbUser = task.getResult().getUser();
                String name = (fbUser != null && fbUser.getDisplayName() != null) ? fbUser.getDisplayName() : "";

                // Successo online: salviamo localmente per la prossima volta che saremo offline
                User newUser = new User(name, "", "", email, password);
                repository.saveLocalUser(newUser, () -> 
                    _loginState.postValue(new LoginState.Success(name, "", "", email, password))
                );
            } else {
                handleFirebaseError(task.getException(), email, password);
            }
        });
    }

    private void handleFirebaseError(Exception e, String email, String password) {
        if (e instanceof FirebaseAuthException) {
            String code = ((FirebaseAuthException) e).getErrorCode();
            
            // ERRORI DI CREDENZIALI → NON usare login locale
            if (code.equals("ERROR_INVALID_CREDENTIAL") || 
                code.equals("ERROR_USER_NOT_FOUND") || 
                code.equals("ERROR_WRONG_PASSWORD") || 
                code.equals("ERROR_USER_DISABLED") || 
                code.equals("ERROR_INVALID_EMAIL")) {
                _loginState.postValue(new LoginState.Error("Credenziali errate"));
                return;
            }
            
            // ERRORE DI RETE → prova login locale
            if (code.equals("ERROR_NETWORK_REQUEST_FAILED")) {
                repository.findLocalUser(email, password, localUser -> {
                    if (localUser != null) {
                        _loginState.postValue(new LoginState.Success(
                            localUser.getName(), localUser.getSurname(), localUser.getAddress(), email, password));
                    } else {
                        _loginState.postValue(new LoginState.Error("Credenziali errate"));
                    }
                });
                return;
            }
        }
        
        // FALLBACK: Gestisce tutti gli altri errori imprevisti
        _loginState.postValue(new LoginState.Error("Errore di autenticazione"));
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
                _loginState.postValue(new LoginState.Error(getApplication().getString(R.string.error_google_login)));
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
