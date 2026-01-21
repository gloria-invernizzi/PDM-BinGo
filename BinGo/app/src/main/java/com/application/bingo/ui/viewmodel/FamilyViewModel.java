package com.application.bingo.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.bingo.model.User;
import com.application.bingo.repository.UserRepository;

import java.util.List;
import java.util.UUID;

/**
 * ViewModel for FamilyFragment.
 * Manages family group operations such as creating, joining, and leaving a family.
 */
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

    /**
     * Creates a new family group and generates a unique code.
     * 
     * @param email The email of the user creating the family.
     */
    public void createFamily(String email) {
        // Generate a short 8-character unique code
        String newFamilyId = UUID.randomUUID().toString().substring(0, 8).toUpperCase(); 
        userRepository.updateFamilyId(email, newFamilyId, new UserRepository.Callback() {
            @Override
            public void onSuccess(String msg) {
                familyId.postValue(newFamilyId);
                loadFamilyMembers(newFamilyId);
                successMessage.postValue("Family created: " + newFamilyId);
            }

            @Override
            public void onFailure(String msg) {
                error.postValue("Error creating family: " + msg);
            }
        });
    }

    /**
     * Joins an existing family group using a family code.
     * 
     * @param email          The email of the user joining the family.
     * @param familyIdToJoin The code of the family to join.
     */
    public void joinFamily(String email, String familyIdToJoin) {
        if (familyIdToJoin == null || familyIdToJoin.isEmpty()) {
            error.postValue("Invalid family code");
            return;
        }
        
        userRepository.getUsersByFamilyId(familyIdToJoin, members -> {
            if (members != null && !members.isEmpty()) {
                 userRepository.updateFamilyId(email, familyIdToJoin, new UserRepository.Callback() {
                    @Override
                    public void onSuccess(String msg) {
                        familyId.postValue(familyIdToJoin);
                        loadFamilyMembers(familyIdToJoin);
                        successMessage.postValue("Successfully joined the family");
                    }

                    @Override
                    public void onFailure(String msg) {
                        error.postValue("Error joining family: " + msg);
                    }
                });
            } else {
                 error.postValue("No family found with this code");
            }
        });
    }
    
    /**
     * Removes the user from their current family group.
     * 
     * @param email The email of the user leaving the family.
     */
    public void leaveFamily(String email) {
        userRepository.updateFamilyId(email, null, new UserRepository.Callback() {
            @Override
            public void onSuccess(String msg) {
                familyId.postValue(null);
                familyMembers.postValue(null);
                successMessage.postValue("You have left the family");
            }

            @Override
            public void onFailure(String msg) {
                error.postValue("Error leaving family: " + msg);
            }
        });
    }

    /**
     * Loads the list of members for a specific family.
     */
    public void loadFamilyMembers(String familyId) {
        if (familyId == null) {
            familyMembers.postValue(null);
            return;
        }
        userRepository.getUsersByFamilyId(familyId, familyMembers::postValue);
    }
    
    /**
     * Checks if the user is already part of a family and loads its details.
     */
    public void checkUserFamily(String email) {
        userRepository.getUser(email, new UserRepository.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                if (user != null && user.getFamilyId() != null) {
                    familyId.postValue(user.getFamilyId());
                    loadFamilyMembers(user.getFamilyId());
                } else {
                    familyId.postValue(null);
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                familyId.postValue(null);
                error.postValue(errorMsg);
            }
        });
    }
}
