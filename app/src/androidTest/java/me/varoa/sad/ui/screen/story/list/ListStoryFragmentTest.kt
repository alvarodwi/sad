package me.varoa.sad.ui.screen.story.list

import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import me.varoa.sad.R
import me.varoa.sad.utils.EspressoIdlingResource
import me.varoa.sad.utils.launchFragmentInHiltContainer
import org.assertj.core.api.Assertions
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
@HiltAndroidTest
class ListStoryFragmentTest {
  @get:Rule
  val hiltAndroidRule = HiltAndroidRule(this)

  @Before
  fun setUp() {
    IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
  }

  @After
  fun tearDown() {
    IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
  }

  @Test
  fun logout() {
    val navController = TestNavHostController(
      ApplicationProvider.getApplicationContext()
    )

    launchFragmentInHiltContainer<ListStoryFragment> {
      this.also { fragment ->
        val graph = navController.navInflater.inflate(R.navigation.nav_main)
        graph.setStartDestination(R.id.list_story)
        navController.setGraph(graph, requireActivity().intent.extras)

        Navigation.setViewNavController(fragment.requireView(), navController)
      }
    }

    // check whether the view is visible
    Espresso.onView(withId(R.id.bottom_app_bar))
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    // perform logout
    Espresso.onView(withId(R.id.action_logout)).perform(ViewActions.click())

    // wait a bit
    Espresso.onView(ViewMatchers.isRoot()).perform(waitFor(2000))

    // assert navController has moved into login
    Assertions.assertThat(navController.currentDestination?.id).isEqualTo(R.id.login)
  }

  private fun waitFor(delay: Long): ViewAction? {
    return object : ViewAction {
      override fun getConstraints(): Matcher<View> = ViewMatchers.isRoot()
      override fun getDescription(): String = "wait for $delay milliseconds"
      override fun perform(uiController: UiController, v: View?) {
        uiController.loopMainThreadForAtLeast(delay)
      }
    }
  }
}