package com.application.bingo.util;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class CredentialUtils {
    /**
     * Apre le impostazioni di sistema per permettere all'utente di aggiungere
     * un nuovo account Google al dispositivo.
     */
    public static void promptToAddGoogleAccount(Context context) {
        Intent intent = new Intent(Settings.ACTION_ADD_ACCOUNT);
        intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[]{"com.google"});
        context.startActivity(intent);
    }
}
