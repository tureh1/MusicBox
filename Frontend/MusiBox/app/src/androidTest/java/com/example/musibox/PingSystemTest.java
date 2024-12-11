package com.example.musibox;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(AndroidJUnit4.class)
public class PingSystemTest {

    @Before
    public void setUp() {

    }
    private static final Logger log = LoggerFactory.getLogger(PingSystemTest.class);
    @Rule
    public ActivityScenarioRule<LoginActivity> loginActivityRule = new ActivityScenarioRule<>(LoginActivity.class);
    @Rule
    public ActivityScenarioRule<SignUp> signUpActivityScenarioRule = new ActivityScenarioRule<>(SignUp.class);

    @Rule
    public ActivityScenarioRule<CreateGroupActivity> CreateActivityScenarioRule = new ActivityScenarioRule<>(CreateGroupActivity.class);

    @Rule
    public ActivityTestRule<MessageActivity> activityRule = new ActivityTestRule<>(MessageActivity.class);

    @Rule
    public ActivityTestRule<CreateGroupActivity> createGroupActivityActivityTestRule = new ActivityTestRule<>(CreateGroupActivity.class);
    @Rule
    public ActivityTestRule<UserProfileActivity> userprofileAcitivityrule = new ActivityTestRule<>(UserProfileActivity.class);

    private void logIn() {
        ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class);

        onView(withId(R.id.username)).perform(typeText("test"));
        onView(withId(R.id.password)).perform(typeText("password"));
        onView(withId(R.id.loginButton)).perform(click());


    }

    @Test
    public void testSearchBar() {
        logIn();
        // Navigate to MessageActivity
        onView(withId(R.id.navigation_message)).perform(click());

        // Ensure that the search bar is visible
        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));

        // Type "user1" in the search bar
        onView(withId(R.id.search_bar))
                .perform(typeText("user1"));


    }
    @Test
    public void userProfileNavigation() {
        logIn();

        onView(withId(R.id.navigation_user)).perform(click());
        // Click the home button
        onView(withId(R.id.user)).perform(click());

        // Verify if the MainPage activity is launched
        onView(ViewMatchers.withId(R.id.settings)) // assuming MainPage has this ID
                .check(matches(ViewMatchers.isDisplayed()));
    }


    @Test
    public void testGroupButtonNavigation() {
        logIn();
        // Navigate to MessageActivity
        onView(withId(R.id.navigation_message)).perform(click());
        // Click the home button
        onView(withId(R.id.adduser)).perform(click());

        // Verify if the MainPage activity is launched
        onView(ViewMatchers.withId(R.id.search_bar)) // assuming MainPage has this ID
                .check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testHomeButtonNavigation() {
        logIn();
        // Navigate to MessageActivity
        onView(withId(R.id.navigation_message)).perform(click());
        // Click the home button
        onView(withId(R.id.home)).perform(click());

        // Verify if the MainPage activity is launched
        onView(ViewMatchers.withId(R.id.search_barAlbum))
                .check(matches(ViewMatchers.isDisplayed()));
    }
    @Test
    public void signUp() {
        ActivityScenario<SignUp> scenario = ActivityScenario.launch(SignUp.class);
        onView(withId(R.id.email)).perform(typeText("test1"));
        onView(withId(R.id.password)).perform(typeText("password"));
        onView(withId(R.id.confirm)).perform(typeText("password"));
        onView(withId(R.id.SignUpButton)).perform(click());

    }


    @Test
    public void testGroupSearchBar() {
        logIn();
        // Navigate to MessageActivity
        onView(withId(R.id.navigation_adduser)).perform(click());

        // Ensure that the search bar is visible
        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));

        // Type "user1" in the search bar
        onView(withId(R.id.search_bar))
                .perform(typeText("user1"));


    }
}
