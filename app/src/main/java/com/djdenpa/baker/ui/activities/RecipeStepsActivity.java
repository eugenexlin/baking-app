package com.djdenpa.baker.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.Recipe;
import com.djdenpa.baker.ui.fragments.StepDetailFragment;
import com.djdenpa.baker.ui.fragments.StepListFragment;

/**
 * Created by denpa on 10/29/2017.
 */

public class RecipeStepsActivity extends AppCompatActivity implements StepListFragment.OnStepClickListener {

  public static final String RECIPE_EXTRA = "StepListFragment_RECIPE_EXTRA";

  Recipe mRecipe = null;
  private StepListFragment mStepListFragment;
  private StepDetailFragment mStepDetailFragment;

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

    mStepDetailFragment = (StepDetailFragment) fm.findFragmentById(R.id.step_detail_fragment);
    if (mStepDetailFragment != null){
      mStepDetailFragment.setRecipe(mRecipe);
    }

  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {

    //There is some stink where on orientation change, the fragment is stuck in the manager
    // Then we get some exception where fragment is not attached..
    // after many hours, it seems the cleanest way is to never save this fragment into state.
    if (mStepDetailFragment != null) {
      FragmentManager fm = getSupportFragmentManager();
      FragmentTransaction ft = fm.beginTransaction();
      ft.remove(mStepDetailFragment);
      ft.commit();
    }

    super.onSaveInstanceState(outState);
  }

  @Override
  public void onStepClicked(int position) {

    if (mStepDetailFragment != null && mStepDetailFragment.isAdded()){
      //has detail fragment
      mStepDetailFragment.bindStep(position);
    }else{
      //does not have detail fragment
      //load activity
      Class destinationClass = StepDetailActivity.class;
      Intent intentToStartDetailActivity = new Intent(this, destinationClass);

      intentToStartDetailActivity.putExtra(StepDetailActivity.RECIPE_EXTRA, mRecipe);
      intentToStartDetailActivity.putExtra(StepDetailActivity.STEP_INDEX_EXTRA, position );
      this.startActivity(intentToStartDetailActivity);
    }
  }

}
