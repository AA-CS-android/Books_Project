package com.hw.books_project;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.hw.books_project.models.Library;
import com.hw.books_project.models.User;
import com.hw.books_project.screens.library.LibraryViewActivity;
import com.hw.books_project.utils.FBRef;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

@RunWith(AndroidJUnit4.class)
public class AddBookTest {

    private Library mockLibrary;

    @Before
    public void setup() {
        // Mock a library object to be used in the intent
        mockLibrary = new Library();
        mockLibrary.setName("Test Library");
        mockLibrary.setAdmin("test_admin_uid");

        // Mock a user and set it as the current user
        User mockUser = new User();
        mockUser.setUid("test_admin_uid");
        FBRef.currentUser = mockUser;
    }

    @Test
    public void testAddBook() {
        // Create an intent with the mock library
        Intent intent = new Intent(androidx.test.core.app.ApplicationProvider.getApplicationContext(), LibraryViewActivity.class);
        intent.putExtra("library", mockLibrary);

        // Launch the activity with the intent
        try (ActivityScenario<LibraryViewActivity> scenario = ActivityScenario.launch(intent)) {
            // Click on the FAB to add a new book
            Espresso.onView(ViewMatchers.withId(R.id.fabAddBook)).perform(ViewActions.click());

            // In AddBookActivity, fill in the book details
            Espresso.onView(ViewMatchers.withId(R.id.etBookName)).perform(ViewActions.typeText("The Hobbit"), ViewActions.closeSoftKeyboard());
            Espresso.onView(ViewMatchers.withId(R.id.etAuthor)).perform(ViewActions.typeText("J.R.R. Tolkien"), ViewActions.closeSoftKeyboard());
            Espresso.onView(ViewMatchers.withId(R.id.etGenres)).perform(ViewActions.typeText("Fantasy"), ViewActions.closeSoftKeyboard());
            Espresso.onView(ViewMatchers.withId(R.id.etISBN)).perform(ViewActions.typeText("9780345339706"), ViewActions.closeSoftKeyboard());

            // Click the save button
            Espresso.onView(ViewMatchers.withId(R.id.btnAddBook)).perform(ViewActions.click());

            // Verify that the new book is displayed in the list
            Espresso.onView(withText("The Hobbit")).check(matches(isDisplayed()));
        }
    }
}
