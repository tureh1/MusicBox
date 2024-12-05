package com.example.musibox;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class BaeSystemTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);


    /**
     * Test Case 1: Test Login Button Visibility
     */
    @Test
    public void testLoginButtonVisibility() {
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));
    }

    /**
     * Test Case 2: Test Login with Valid Credentials
     */
    @Test
    public void testLoginWithValidCredentials() {
        onView(withId(R.id.username)).perform(typeText("bae"));
        onView(withId(R.id.password)).perform(typeText("password"));
        onView(withId(R.id.loginButton)).perform(click());

        // Verify that the MainPage activity is started.
        onView(withText("MainPage")).check(matches(isDisplayed()));
    }

    /**
     * Test Case 3: Test Login with Invalid Credentials
     */
    @Test
    public void testLoginWithInvalidCredentials() {
        onView(withId(R.id.username)).perform(typeText("invalidemail@example.com"));
        onView(withId(R.id.password)).perform(typeText("wrongpassword"));
        onView(withId(R.id.loginButton)).perform(click());

        // Check that an error message is displayed.
        onView(withText("Invalid credentials")).check(matches(isDisplayed()));
    }

    /**
     * Test Case 4: Test Login with Empty Fields
     */
    @Test
    public void testLoginWithEmptyFields() {
        onView(withId(R.id.username)).perform(typeText(""));
        onView(withId(R.id.password)).perform(typeText(""));
        onView(withId(R.id.loginButton)).perform(click());

        // Check that a message asking the user to fill all fields is displayed.
        onView(withText("Please fill in all fields")).check(matches(isDisplayed()));
    }

}
