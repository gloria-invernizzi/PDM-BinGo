package com.application.bingo.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.application.bingo.R;
import com.application.bingo.repository.SettingsRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.Locale;

/**
 * MainActivity:
 * The main activity hosting the navigation graph, toolbar, and bottom navigation.
 * Handles global settings like theme, language, and Firestore persistence.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the saved language before creating the UI
        applySavedLanguage();

        // Handle dynamic theme (dark/light mode)
        SettingsRepository settingsRepo = new SettingsRepository(this);

        if (settingsRepo.isDarkTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Enable Firestore persistence for offline data access
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Apply window insets for edge-to-edge support
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top,
                    systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup NavController
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);

        if (navHostFragment == null) return;
        NavController navController = navHostFragment.getNavController();

        // Setup Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();

            // Hide Toolbar for all destinations in this setup
            toolbar.setVisibility(View.GONE);

            // List of fragments where Bottom Navigation should be hidden
            if (id == R.id.welcomeFragment ||
                    id == R.id.loginFragment ||
                    id == R.id.registerFragment ||
                    id == R.id.recoverPasswordFragment) {
                bottomNav.setVisibility(View.GONE);
            } else {
                bottomNav.setVisibility(View.VISIBLE);
            }
        });

        // Top-level destinations (no back button in action bar)
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(
                        R.id.homeFragment,
                        R.id.calendarFragment,
                        R.id.favoriteFragment,
                        R.id.profileFragment
                ).build();

        NavigationUI.setupWithNavController(bottomNav, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // -------------------- NOTIFICATION PERMISSION MANAGEMENT --------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            } else {
                // Permission already granted: update settings
                settingsRepo.setNotificationsEnabled(true);
            }
        }
    }

    // -------------------- PERMISSION RESULT CALLBACK --------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && permissions.length > 0) {
            SettingsRepository settingsRepo = new SettingsRepository(this);
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                settingsRepo.setNotificationsEnabled(true);
            } else {
                settingsRepo.setNotificationsEnabled(false);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }

    /**
     * Applies the saved language preference to the activity configuration.
     */
    private void applySavedLanguage() {
        SettingsRepository settingsRepo = new SettingsRepository(this);
        String lang = settingsRepo.getLanguage();

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Resources res = getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);

        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}
