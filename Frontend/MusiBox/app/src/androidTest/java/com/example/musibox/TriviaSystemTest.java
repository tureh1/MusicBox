package com.example.musibox;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TriviaSystemTest {

    @Rule
    public ActivityScenarioRule<TriviaActivity> activityRule = new ActivityScenarioRule<>(TriviaActivity.class);

    @Test
    public void testTriviaFlow() throws InterruptedException {
        // Allow time for the first question to load
        Thread.sleep(2000); // Adjust this duration as needed

        // Verify that the first question is displayed
        onView(withId(R.id.question)).check(matches(isDisplayed()));

        // Simulate selecting an answer
        onView(withId(R.id.choice1)).perform(click());

        // Allow time for the next question to load
        Thread.sleep(2000);

        // Click the next button
        onView(withId(R.id.nextButton)).perform(click());

        // Verify the second question is displayed
        onView(withId(R.id.question)).check(matches(isDisplayed()));
    }

    @Test
    public void testAnswer() throws InterruptedException {
        // Allow time for the UI to load
        Thread.sleep(2000);

        // Verify the correct answer feedback is displayed
        onView(withId(R.id.choice1)).perform(click());

    }

    @Test
    public void testResultsDialog() throws InterruptedException {
        // Simulate answering all 5 questions
        for (int i = 0; i < 5; i++) {
            onView(withId(R.id.choice1)).perform(click());
            Thread.sleep(1000); // Allow time for feedback or UI updates
            onView(withId(R.id.nextButton)).perform(click());
        }

        // Allow time for the results dialog to load
        Thread.sleep(2000);

        // Verify that the results dialog is displayed
        onView(withText("Trivia Results")).check(matches(isDisplayed()));

        // Click on "Try Again"
        onView(withText("Try Again")).perform(click());

        // Verify the first question is displayed again
        onView(withId(R.id.question)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackButtonNavigation() throws InterruptedException {
        // Allow time for the UI to load
        Thread.sleep(2000);

        // Click the back button
        onView(withId(R.id.backArrow)).perform(click());

    }
}