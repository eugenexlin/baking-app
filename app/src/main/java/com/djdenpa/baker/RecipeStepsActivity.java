package com.djdenpa.baker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.djdenpa.baker.core.Recipe;
import com.djdenpa.baker.core.Step;
import com.djdenpa.baker.ui.fragments.StepDetailFragment;
import com.djdenpa.baker.ui.fragments.StepListFragment;

/**
 * Created by denpa on 10/29/2017.
 */

public class RecipeStepsActivity extends AppCompatActivity implements StepListFragment.OnStepClickListener {

  public static final String RECIPE_EXTRA = "StepListFragment_RECIPE_EXTRA";

  StepListFragment mStepListFragment;
  StepDetailFragment mStepDetailFragment;
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
    mStepListFragment.bindRecipe(mRecipe);

    //for tablets. we will have the step detail fragment on the same page.
    mStepDetailFragment = (StepDetailFragment) fm.findFragmentById(R.id.step_detail_fragment);
  }


  @Override
  public void onStepClicked(Step selectedStep) {

    if (mStepDetailFragment != null){
      //has detail fragment
      mStepDetailFragment.bindStep(selectedStep);
    }else{
      //does not have detail fragment
      //load activity
      Class destinationClass = StepDetailActivity.class;
      Intent intentToStartDetailActivity = new Intent(this, destinationClass);

      intentToStartDetailActivity.putExtra(StepDetailActivity.STEP_EXTRA, selectedStep);
      this.startActivity(intentToStartDetailActivity);
    }
  }

}
