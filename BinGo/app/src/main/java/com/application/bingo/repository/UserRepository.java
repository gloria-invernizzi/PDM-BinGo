package com.application.bingo.repository;

import android.content.Context;

import com.application.bingo.AppDatabase;
import com.application.bingo.User;
import com.application.bingo.UserDao;

public class UserRepository {
    /*recupero dell’utente dal database
    aggiornamento dell’utente
    non si preoccupa di
     come è fatto il database
    quale DAO chiamare
    come è strutturata la query*/

    private final UserDao userDao;

    public UserRepository(Context context){
        userDao= AppDatabase.getInstance(context).userDao();
    }

    public User getUser(String email){
        return userDao.findByEmail(email);
    }

    public void updateUser(User user){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.update(user);  // questa operazione va su un thread separato
        });

    }



}
