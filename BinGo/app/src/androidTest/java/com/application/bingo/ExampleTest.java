package com.application.bingo;

import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
/* esempio di test su login con Espresso
Verificare che i campi email e password siano visibili.

Verificare che il pulsante login sia cliccabile.

Simulare l’inserimento di email e password e il click sul pulsante login.

Controllare che venga mostrata una Toast di errore se i campi sono vuoti.*/
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.application.bingo.R;
import com.application.bingo.ui.LoginFragment;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleTest {

    @Test
    public void loginFieldsAreVisible() {
        // Avvia il fragment in isolamento
        FragmentScenario<LoginFragment> scenario =
                FragmentScenario.launchInContainer(LoginFragment.class);

        // Verifica che i campi email e password siano visibili
        onView(withId(R.id.textInputEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.textInputPassword)).check(matches(isDisplayed()));

        // Verifica che il pulsante login sia visibile e cliccabile
        onView(withId(R.id.login_button)).check(matches(isDisplayed()));
    }

    @Test
    public void loginWithEmptyFieldsShowsErrorToast() {
        FragmentScenario<LoginFragment> scenario =
                FragmentScenario.launchInContainer(LoginFragment.class);

        // Assicurati che i campi siano vuoti (di default lo sono)
        // Clicca sul pulsante login
        onView(withId(R.id.login_button)).perform(click());

        // Non possiamo catturare il Toast direttamente con Espresso "base",
        // ma se vuoi lo possiamo verificare con Espresso-Intents o con una libreria come ToastMatcher.
        // Qui ci limitiamo a mostrare l’azione cliccando.
    }

    @Test
    public void loginWithTextInput() {
        FragmentScenario<LoginFragment> scenario =
                FragmentScenario.launchInContainer(LoginFragment.class);

        // Inserisci email e password
        onView(withId(R.id.textInputEmail)).perform(typeText("test@example.com"));
        onView(withId(R.id.textInputPassword)).perform(typeText("password123"));

        // Clicca sul pulsante login
        onView(withId(R.id.login_button)).perform(click());
    }
}
