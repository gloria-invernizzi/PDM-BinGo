package com.application.bingo.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.repository.UserRepository;

/**
 * ViewModel per gestire la logica di cambio password.
 * - Comunica SOLO col Repository .
 * - Non conosce dettagli del DB o Firebase.
 * - Espone messaggi al Fragment tramite LiveData.
 */
public class ChangePasswordViewModel extends ViewModel {

    private final UserRepository userRepository;

    // LiveData osservata dal Fragment per mostrare messaggi (successo/errore)
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();

    public ChangePasswordViewModel(UserRepository repository) {
        this.userRepository = repository; // repository viene iniettato → ottimo per test e MVVM
    }

    // Restituisce la LiveData che il fragment osserverà
    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    /**
     * Richiesta di cambio password.
     *
     * @param userEmail       email dell'utente (recuperata da SharedPreferences)
     * @param oldPassword     vecchia password inserita dall’utente
     * @param newPassword     nuova password inserita
     * @param confirmPassword conferma della nuova password
     *
     * - Non fa controlli logici → li fa già il Fragment.
     * - Non tocca Room o Firebase → lo fa il Repository.
     */
    public void changePassword(String userEmail, String oldPassword, String newPassword, String confirmPassword) {

        userRepository.changePassword(
                userEmail,
                oldPassword,
                newPassword,
                confirmPassword,
                new UserRepository.Callback() {

                    @Override
                    public void onSuccess(String message) {

                        // Se il repository ritorna PASSWORD_OK → mappiamo in un messaggio leggibile
                        if (UserRepository.PASSWORD_OK.equals(message)) {
                            messageLiveData.postValue("Password aggiornata con successo");
                        } else {
                            // fallback in caso di altri messaggi (teoricamente non più usati)
                            messageLiveData.postValue(message);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        // Inoltra l'errore al fragment
                        messageLiveData.postValue(error);
                    }
                }
        );
    }
}
