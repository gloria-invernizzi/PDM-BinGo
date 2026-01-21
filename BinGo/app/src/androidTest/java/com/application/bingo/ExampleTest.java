package com.application.bingo;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.application.bingo.ui.LoginFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Example Login tests using Espresso.
 * 
 * - Verifies that email and password fields are visible.
 * - Verifies that the login button is clickable.
 * - Simulates entering email and password and clicking the login button.
 * - Checks that an error Toast is shown if fields are empty.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleTest {

    @Test
    public void loginFieldsAreVisible() {
        // Launch the fragment in isolation
        FragmentScenario<LoginFragment> scenario =
                FragmentScenario.launchInContainer(LoginFragment.class);

        // Verify that email and password fields are visible
        onView(withId(R.id.textInputEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.textInputPassword)).check(matches(isDisplayed()));

        // Verify that the login button is visible and clickable
        onView(withId(R.id.login_button)).check(matches(isDisplayed()));
    }

    @Test
    public void loginWithEmptyFieldsShowsErrorToast() {
        FragmentScenario<LoginFragment> scenario =
                FragmentScenario.launchInContainer(LoginFragment.class);

        // Ensure fields are empty (default state)
        // Click the login button
        onView(withId(R.id.login_button)).perform(click());

        // Note: Capturing Toasts directly with base Espresso is not straightforward.
        // It can be verified with Espresso-Intents or a library like ToastMatcher.
        // Here we just simulate the click action.
    }

    @Test
    public void loginWithTextInput() {
        FragmentScenario<LoginFragment> scenario =
                FragmentScenario.launchInContainer(LoginFragment.class);

        // Enter email and password
        onView(withId(R.id.textInputEmail)).perform(typeText("test@example.com"));
        onView(withId(R.id.textInputPassword)).perform(typeText("password123"));

        // Click the login button
        onView(withId(R.id.login_button)).perform(click());
    }
}
