package com.djdenpa.baker.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.Recipe;
import com.djdenpa.baker.ui.fragments.StepDetailsFragment;

/**
 * Created by denpa on 11/5/2017.
 * Activity for viewing the set detail and video.
 */

public class StepDetailActivity
        extends AppCompatActivity
        implements StepDetailsFragment.SetStepIndexHandler{

  public static final String RECIPE_EXTRA = "StepDetailActivity_RECIPE_EXTRA";
  public static final String STEP_INDEX_EXTRA = "StepDetailActivity_STEP_INDEX_EXTRA";

  private static final String STEP_INDEX_STATE = "StepDetailActivity_STEP_INDEX_STATE";
  private Recipe mRecipe;
  private int mStepIndex;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_step_details);

    Intent parentIntent = getIntent();

    if (parentIntent != null) {
      if (parentIntent.hasExtra(RECIPE_EXTRA)) {
        mRecipe = parentIntent.getParcelableExtra(RECIPE_EXTRA);
      }
      if (parentIntent.hasExtra(STEP_INDEX_EXTRA)) {
        mStepIndex = parentIntent.getIntExtra(STEP_INDEX_EXTRA,0);
      }
    }


    FragmentManager fm = getSupportFragmentManager();
    StepDetailsFragment stepDetailFragment = (StepDetailsFragment) fm.findFragmentById(R.id.step_detail_fragment);

    if(savedInstanceState != null){
      if (savedInstanceState.containsKey(STEP_INDEX_STATE)){
        mStepIndex = savedInstanceState.getInt(STEP_INDEX_STATE);
        stepDetailFragment.skipOneExoBind();
      }
    }

    if (mRecipe == null || mRecipe.steps() == null || mStepIndex >= mRecipe.steps().size()){
      stepDetailFragment.displayError(getString(R.string.cannot_load_recipe_error));
      return;
    }

    stepDetailFragment.setRecipe(mRecipe);
    stepDetailFragment.bindStep(mStepIndex);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putInt(STEP_INDEX_STATE, mStepIndex);
  }


  @Override
  public void handleSetStepIndex(int index) {
    String titleTemplate = getString(R.string.step_detail_title_template);
    mStepIndex = index;
    setTitle(String.format(titleTemplate, String.valueOf(index + 1)));
  }
}
