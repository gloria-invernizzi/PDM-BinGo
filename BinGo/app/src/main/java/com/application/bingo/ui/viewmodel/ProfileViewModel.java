package com.application.bingo.ui.viewmodel;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.fragment.NavHostFragment;

import com.application.bingo.R;
import com.application.bingo.model.User;
import com.application.bingo.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * ProfileViewModel:
 * - Maintains user state (name, photo, email, etc.)
 * - Communicates with the repository for all data operations
 * - Decoupled from specific data sources (DB, Firebase)
 * - Exposes LiveData to the UI (Fragment)
 */
public class ProfileViewModel extends ViewModel {

    private final UserRepository userRepo;

    // LiveData observed by the Fragment
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> deleteAccountResult = new MutableLiveData<>();

    public ProfileViewModel(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getDeleteAccountResult() {
        return deleteAccountResult;
    }

    /**
     * Loads user data from the repository using the provided email.
     *
     * @param email The user's email address.
     */
    public void loadUser(String email) {
        Log.d("ProfileViewModel", "loadUser called with email = " + email);
        userRepo.getUser(email, new UserRepository.UserCallback() {
            @Override
            public void onUserLoaded(User u) {
                if (u != null) {
                    Log.d("ProfileViewModel", "User received from UserLocalSource: " + u);
                    user.postValue(u);
                } else {
                    error.postValue("User not found");
                }
            }

            @Override
            public void onFailure(String msg) {
                error.postValue(msg);
            }
        });
    }

    public void loadLoggedUser() {
        String email = userRepo.getCurrentUserEmail();
        if (email == null) {
            error.postValue("user_not_loaded");
            return;
        }
        loadUser(email);
    }

    // ---------------------------------------------------------------------------------------------
    // SALVA MODIFICHE NOME/INDIRIZZO
    // ---------------------------------------------------------------------------------------------
    /**
     * Updates profile information (name and address) in both remote and local
     * storage.
     *
     * @param name    The new user name.
     * @param address The new user address.
     */
    public void updateProfile(String name, String address) {
        User u = user.getValue();
        if (u == null) {
            error.postValue("user_not_loaded");
            return;
        }
        u.setName(name);
        u.setAddress(address);
        userRepo.updateUser(u); // Update Room/Remote
        userRepo.saveToPrefs(u); // Update PrefsManager
        user.postValue(u);
    }

    /**
     * Updates and saves the user's profile photo URI.
     *
     * @param email The user's email.
     * @param uri   The URI of the new profile photo.
     */
    public void savePhotoUri(String email, String uri) {
        User u = user.getValue();
        if (u == null) {
            error.postValue("user_not_found");
            return;
        }
        u.setPhotoUri(uri);
        userRepo.updatePhotoUri(email, uri);
        userRepo.saveToPrefs(u); // Update PrefsManager
        user.postValue(u);
    }

    /**
     * Deletes the user account.
     * Checks for internet availability and handles re-authentication requirements.
     */
    public void deleteAccount() {
        User u = user.getValue();
        if (u == null) {
            deleteAccountResult.postValue("user_not_loaded");
            return;
        }

        if (!userRepo.isInternetAvailable()) {
            deleteAccountResult.postValue("offline_error");
            return;
        }
        userRepo.deleteAccount(user.getValue(), new UserRepository.Callback() {
            @Override
            public void onSuccess(String message) {
                deleteAccountResult.postValue("account_deleted_success");
            }

            @Override
            public void onFailure(String error) {
                // Can be "reauth_required" or other Firebase specific errors
                deleteAccountResult.postValue(error);
            }
        });
    }
}
