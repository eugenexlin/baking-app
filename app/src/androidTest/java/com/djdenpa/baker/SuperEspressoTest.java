package com.djdenpa.baker;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.djdenpa.baker.core.BakingApplication;
import com.djdenpa.baker.core.Ingredient;
import com.djdenpa.baker.core.Recipe;
import com.djdenpa.baker.core.Step;
import com.djdenpa.baker.ui.activities.MainActivity;

import org.json.JSONArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by denpa on 12/10/2017.
 * ui unit tests
 */

@RunWith(AndroidJUnit4.class)
public class SuperEspressoTest {

  private static final String TEST_RECIPE_NAME = "Super Cupcake";
  private static final String TEST_RECIPE_STEP_DESCRIPTION_SHORT = "Make It Babe";
  private static final String TEST_RECIPE_STEP_DESCRIPTION = "First, make the cupcake so moist, it quenches your thirst. That is all.";
  private static final int TEST_RECIPE_SERVINGS = 8;

  @Rule
  public final ActivityTestRule<MainActivity> mActivityTestRule =
          new ActivityTestRule<>(MainActivity.class);

  private IdlingResource mMainActivityIdlingResource;

  public Recipe getTestRecipe() {
    Recipe testRecipe = new Recipe();
    testRecipe.name = TEST_RECIPE_NAME;
    //put one step
    Step testStep = new Step();
    testStep.shortDescription = TEST_RECIPE_STEP_DESCRIPTION_SHORT;
    testStep.description = TEST_RECIPE_STEP_DESCRIPTION;
    testStep.id = 0;
    JSONArray jSteps = new JSONArray();
    jSteps.put(Step.toJSON(testStep));
    testRecipe.stepsJSON = jSteps.toString();
    //put one ingredient
    Ingredient testIngredient = new Ingredient();
    testIngredient.ingredient = TEST_RECIPE_STEP_DESCRIPTION_SHORT;
    testIngredient.measure = TEST_RECIPE_STEP_DESCRIPTION;
    testIngredient.quantity = 0;
    JSONArray jIngredients = new JSONArray();
    jIngredients.put(Ingredient.toJSON(testIngredient));
    testRecipe.ingredientsJSON = jIngredients.toString();
    testRecipe.servings = TEST_RECIPE_SERVINGS;

    return testRecipe;
  }

  @Before
  public void setUp() {
    mMainActivityIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
    IdlingRegistry.getInstance().register(mMainActivityIdlingResource);
  }

  @After
  public void tearDown() {
    if (mMainActivityIdlingResource != null) {
      IdlingRegistry.getInstance().unregister(mMainActivityIdlingResource);
    }
  }

  @Test
  public void TestStepDetails() {
    BakingApplication bakingApp = (BakingApplication) mActivityTestRule.getActivity().getApplication();
    int recipeIndex = 0;
    int stepIndex = 2;
    int nextStepIndex = stepIndex+1;

    onView(withId(R.id.rv_recipe_list))
            .perform(actionOnItemAtPosition(recipeIndex, click()));

    Recipe targetRecipe = bakingApp.mRecipes.get(recipeIndex);
    Step targetStep = targetRecipe.steps().get(stepIndex);
    Step targetNextStep = targetRecipe.steps().get(nextStepIndex);

    //we should navigate to recipe details activity

    onView(withId(R.id.rv_steps))
            .perform(ViewActions.scrollTo())
            .perform(actionOnItemAtPosition(stepIndex, click()));

    //we should navigate to steps details or load the fragment

    onView(withId(R.id.tv_short_description)).check(matches(withText(targetStep.shortDescription)));
    onView(withId(R.id.tv_step_description)).check(matches(withText(targetStep.description)));

    onView(withId(R.id.b_next_step))
            .perform(click());

    onView(withId(R.id.tv_short_description)).check(matches(withText(targetNextStep.shortDescription)));
    onView(withId(R.id.tv_step_description)).check(matches(withText(targetNextStep.description)));
  }


}
