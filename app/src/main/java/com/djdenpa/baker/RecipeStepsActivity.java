package com.djdenpa.baker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.djdenpa.baker.core.Recipe;
import com.djdenpa.baker.ui.fragments.StepListFragment;

/**
 * Created by denpa on 10/29/2017.
 */

public class RecipeStepsActivity extends AppCompatActivity {

  public static final String RECIPE_EXTRA = "StepListFragment_RECIPE_EXTRA";

  StepListFragment mStepListFragment;
  Recipe mRecipe = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recipe_steps);

    Intent parentIntent = getIntent();

    if (parentIntent != null) {
      if (parentIntent.hasExtra(RECIPE_EXTRA)) {
        mRecipe = parentIntent.getParcelableExtra(RECIPE_EXTRA);
      }
    }

    FragmentManager fm = getSupportFragmentManager();
    mStepListFragment = (StepListFragment) fm.findFragmentById(R.id.step_list_fragment);

    if (mRecipe == null){
      mStepListFragment.displayError(getString(R.string.cannot_load_recipe_error));
      return;
    }

    setTitle(mRecipe.name);


  }


}
