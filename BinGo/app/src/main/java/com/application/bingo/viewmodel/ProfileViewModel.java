package com.application.bingo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.database.User;
import com.application.bingo.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * ProfileViewModel:
 * - Mantiene lo stato dell'utente (nome, foto, email...)
 * - Chiama il repository per qualsiasi operazione sui dati
 * - Non conosce database, né Firebase
 * - Espone LiveData al Fragment
 */
public class ProfileViewModel extends ViewModel {

    private final UserRepository userRepo;

    // LiveData osservata dal Fragment
    private final MutableLiveData<User> user = new MutableLiveData<>();

    public ProfileViewModel(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public LiveData<User> getUser() {
        return user;
    }

    // ---------------------------------------------------------------------------------------------
    // CARICA UTENTE DAL REPOSITORY
    // ---------------------------------------------------------------------------------------------
    public void loadUser(String email) {
        userRepo.getUser(email, u -> {
            if (u != null) {
                // trovato in Room → aggiorna LiveData
                user.setValue(u);
            } else {
                // fallback Firebase: usa solo email, senza creare record vuoto in Room
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null && email.equals(firebaseUser.getEmail())) {
                    User fUser = new User(
                            "",       // name vuoto (Firebase displayName spesso nullo)
                            "",       // address vuoto
                            email,    // email corretta
                            ""        // photoUri vuoto
                    );
                    // NON salvare in Room ancora, aggiorna solo LiveData
                    user.setValue(fUser);
                } else {
                    // nessun utente trovato
                    user.setValue(null);
                }
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // SALVA MODIFICHE NOME/INDIRIZZO
    // ---------------------------------------------------------------------------------------------
    public void updateProfile(String name, String address) {

        User u = user.getValue();
        if (u == null) return;

        u.setName(name);
        u.setAddress(address);

        userRepo.updateUser(u);   // update nel database
        user.setValue(u);         // aggiorna la LiveData
    }

    // ---------------------------------------------------------------------------------------------
    // SALVA FOTO PROFILO
    // ---------------------------------------------------------------------------------------------
    public void savePhotoUri(String email, String uri) {

        userRepo.updatePhotoUri(email, uri);

        // aggiorno subito LiveData
        User u = user.getValue();
        if (u != null) {
            u.setPhotoUri(uri);
            user.setValue(u);
        }
    }
}
