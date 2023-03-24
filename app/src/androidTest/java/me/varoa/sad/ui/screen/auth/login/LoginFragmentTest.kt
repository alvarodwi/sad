package me.varoa.sad.ui.screen.auth.login

import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import me.varoa.sad.R
import me.varoa.sad.di.DataModule
import me.varoa.sad.utils.EspressoIdlingResource
import me.varoa.sad.utils.JsonConverter
import me.varoa.sad.utils.launchFragmentInHiltContainer
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {
    @get:Rule
    val hiltAndroidRule = HiltAndroidRule(this)

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        DataModule.BASE_URL = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loginSuccess() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        launchFragmentInHiltContainer<LoginFragment> {
            this.also { fragment ->
                val graph = navController.navInflater.inflate(R.navigation.nav_main)
                graph.setStartDestination(R.id.login)
                navController.setGraph(graph, requireActivity().intent.extras)

                Navigation.setViewNavController(fragment.requireView(), navController)
            }
        }

        // check whether the view is visible
        onView(withId(R.id.ed_login_email)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_password)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))

        // perform login
        onView(withId(R.id.ed_login_email)).perform(clearText(), typeText("test@mail.id"))
        onView(withId(R.id.ed_login_password)).perform(clearText(), typeText("test123456"))
        onView(withId(R.id.btn_login)).perform(click())

        // mock success response
        val mockLoginResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("login_success_response.json"))
        mockWebServer.enqueue(mockLoginResponse)

        // wait a bit
        onView(isRoot()).perform(waitFor(2000))

        // assert navController has moved into list story
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.list_story)
    }

    @Test
    fun loginFailed() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        launchFragmentInHiltContainer<LoginFragment> {
            this.also { fragment ->
                val graph = navController.navInflater.inflate(R.navigation.nav_main)
                graph.setStartDestination(R.id.login)
                navController.setGraph(graph, requireActivity().intent.extras)

                Navigation.setViewNavController(fragment.requireView(), navController)
            }
        }

        // check whether the view is visible
        onView(withId(R.id.ed_login_email)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_password)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))

        // perform login
        onView(withId(R.id.ed_login_email)).perform(clearText(), typeText("test@mail.id"))
        onView(withId(R.id.ed_login_password)).perform(clearText(), typeText("wrongpassword"))
        onView(withId(R.id.btn_login)).perform(click())

        // mock success response
        val mockLoginResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("login_failed_response.json"))
        mockWebServer.enqueue(mockLoginResponse)

        // assert navController hasn't moved from login
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.login)
    }

    private fun waitFor(delay: Long): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }
}
