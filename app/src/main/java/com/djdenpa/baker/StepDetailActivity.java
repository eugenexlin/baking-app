package com.djdenpa.baker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.djdenpa.baker.core.Step;
import com.djdenpa.baker.ui.fragments.StepDetailFragment;

/**
 * Created by denpa on 11/5/2017.
 */

public class StepDetailActivity extends AppCompatActivity {

  public static final String STEP_EXTRA = "StepDetailActivity_STEP_EXTRA";
  private Step mStep;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_step_detail);

    Intent parentIntent = getIntent();

    if (parentIntent != null) {
      if (parentIntent.hasExtra(STEP_EXTRA)) {
        mStep = parentIntent.getParcelableExtra(STEP_EXTRA);
      }
    }

    FragmentManager fm = getSupportFragmentManager();
    StepDetailFragment stepDetailFragment = (StepDetailFragment) fm.findFragmentById(R.id.step_detail_fragment);

    if (mStep == null){
      stepDetailFragment.displayError(getString(R.string.cannot_load_recipe_error));
      return;
    }

    setTitle(mStep.shortDescription);
    stepDetailFragment.bindStep(mStep);

  }


}
