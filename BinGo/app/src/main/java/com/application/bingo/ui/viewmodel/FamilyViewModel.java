package com.application.bingo.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.model.User;
import com.application.bingo.repository.UserRepository;

import java.util.List;
import java.util.UUID;

public class FamilyViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<String> familyId = new MutableLiveData<>();
    private final MutableLiveData<List<User>> familyMembers = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public FamilyViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<String> getFamilyId() {
        return familyId;
    }

    public LiveData<List<User>> getFamilyMembers() {
        return familyMembers;
    }

    public LiveData<String> getError() {
        return error;
    }
    
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    // Crea un nuovo gruppo famiglia
    public void createFamily(String email) {
        String newFamilyId = UUID.randomUUID().toString().substring(0, 8).toUpperCase(); // Codice breve
        userRepository.updateFamilyId(email, newFamilyId, new UserRepository.Callback() {
            @Override
            public void onSuccess(String msg) {
                familyId.postValue(newFamilyId);
                loadFamilyMembers(newFamilyId);
                successMessage.postValue("Famiglia creata: " + newFamilyId);
            }

            @Override
            public void onFailure(String msg) {
                error.postValue("Errore creazione famiglia: " + msg);
            }
        });
    }

    // Unisciti a un gruppo famiglia esistente
    public void joinFamily(String email, String familyIdToJoin) {
        if (familyIdToJoin == null || familyIdToJoin.isEmpty()) {
            error.postValue("Codice famiglia non valido");
            return;
        }
        
        userRepository.getUsersByFamilyId(familyIdToJoin, members -> {
            if (members != null && !members.isEmpty()) {
                 userRepository.updateFamilyId(email, familyIdToJoin, new UserRepository.Callback() {
                    @Override
                    public void onSuccess(String msg) {
                        familyId.postValue(familyIdToJoin);
                        loadFamilyMembers(familyIdToJoin);
                        successMessage.postValue("Unito alla famiglia con successo");
                    }

                    @Override
                    public void onFailure(String msg) {
                        error.postValue("Errore unione famiglia: " + msg);
                    }
                });
            } else {
                 error.postValue("Famiglia non trovata con questo codice");
            }
        });
    }
    
    // Lascia la famiglia
    public void leaveFamily(String email) {
        userRepository.updateFamilyId(email, null, new UserRepository.Callback() {
            @Override
            public void onSuccess(String msg) {
                familyId.postValue(null);
                familyMembers.postValue(null);
                successMessage.postValue("Hai lasciato la famiglia");
            }

            @Override
            public void onFailure(String msg) {
                error.postValue("Errore uscita famiglia: " + msg);
            }
        });
    }

    public void loadFamilyMembers(String familyId) {
        if (familyId == null) {
            familyMembers.postValue(null);
            return;
        }
        userRepository.getUsersByFamilyId(familyId, members -> {
            familyMembers.postValue(members);
        });
    }
    
    public void checkUserFamily(String email) {
        userRepository.getUser(email, user -> {
             if (user != null && user.getFamilyId() != null) {
                 familyId.postValue(user.getFamilyId());
                 loadFamilyMembers(user.getFamilyId());
             } else {
                 familyId.postValue(null);
             }
        });
    }
}
