package com.example.musibox;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;




@RunWith(AndroidJUnit4.class)
public class BaeSystemTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> loginActivityRule = new ActivityScenarioRule<>(LoginActivity.class);

    private ActivityScenario<MainPage> mainPageScenario;

    @Before
    public void setUp() {

    }

    @Test
    public void testLogin() {
        // Launch LoginActivity before each test
        onView(withId(R.id.username)).perform(typeText("test"));
        onView(withId(R.id.password)).perform(typeText("password"));
        onView(withId(R.id.loginButton)).perform(click());

        // After login, MainPage should be launched.
        mainPageScenario = ActivityScenario.launch(MainPage.class);
    }

    @Test
    public void testSongDataFetchedAndDisplayed() {
        // Perform login
        testLogin();

        // Ensure that the song data is displayed correctly in the RecyclerView
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }
    @Test
    public void testRecyclerViewVisibilityOnMainPage() {
        // Perform login
        testLogin();

        // Ensure that the RecyclerView is visible on the MainPage
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchFunctionality() {
        // Perform login
        testLogin();

        // Simulate typing in the search bar
        onView(withId(R.id.search_barAlbum)).perform(typeText("Baby"));
        // Verify that the filtered list is displayed
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testGroupButtonNavigation() {
        testLogin();
        // Navigate to MessageActivity
        // Click the home button
        onView(withId(R.id.navigation_adduser)).perform(click());

        // Verify if the MainPage activity is launched
        onView(ViewMatchers.withId(R.id.search_bar)) // assuming MainPage has this ID
                .check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testHomeButtonNavigation() {
        testLogin();
        // Click the home button
        onView(withId(R.id.navigation_home)).perform(click());

        // Verify if the MainPage activity is launched
        onView(ViewMatchers.withId(R.id.search_barAlbum)) // assuming MainPage has this ID
                .check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void userProfileNavigation() {
        testLogin();
        // Navigate to MessageActivity
        onView(withId(R.id.navigation_user)).perform(click());
        // Click the home button
        onView(withId(R.id.user)).perform(click());

        // Verify if the MainPage activity is launched
        onView(ViewMatchers.withId(R.id.settings)) // assuming MainPage has this ID
                .check(matches(ViewMatchers.isDisplayed()));
    }
}


