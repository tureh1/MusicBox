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
public class SettingsTest {

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

    @Rule
    public ActivityScenarioRule<MainPage> MainPageRule = new ActivityScenarioRule<>(MainPage.class);

    @Rule
    public ActivityScenarioRule<UserProfileActivity> UserProfileRule = new ActivityScenarioRule<>(UserProfileActivity.class);

    @Rule
    public ActivityScenarioRule<SettingsActivity> SettingsRule = new ActivityScenarioRule<>(SettingsActivity.class);

    private void logIn() {
        ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class);

        onView(withId(R.id.username)).perform(typeText("bae"));
        onView(withId(R.id.password)).perform(typeText("password"));
        onView(withId(R.id.loginButton)).perform(click());


    }

    @Test
    public void testRestPasswordFunctionality() {
        logIn();
        onView(withId(R.id.navigation_user)).check(matches(isDisplayed()));
        // Navigate to the profile screen
        onView(withId(R.id.navigation_user)).perform(click());

        // Verify the profile screen is displayed (optional, if it has a unique view)
        onView(withId(R.id.settings)).check(matches(isDisplayed()));

        // Navigate to the settings
        onView(withId(R.id.settings)).perform(click());
        onView(withId(R.id.edit_password)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_password)).perform(typeText("newpass"));
        onView(withId(R.id.confirm_password)).check(matches(isDisplayed()));
        onView(withId(R.id.confirm_password)).perform(typeText("newpass"));
        onView(withId(R.id.update_password_button)).check(matches(isDisplayed()));
        onView(withId(R.id.update_password_button)).perform(click());

        // Verify the settings screen is displayed (optional, if it has a unique view)

        // Verify the user is redirected to LoginActivity
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));
        onView(withId(R.id.username)).perform(typeText("bae"));
        onView(withId(R.id.password)).perform(typeText("newpass"));
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.search_barAlbum)).check(matches(isDisplayed()));
    }
}