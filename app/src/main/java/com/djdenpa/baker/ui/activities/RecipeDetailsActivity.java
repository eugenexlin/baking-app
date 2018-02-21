package com.djdenpa.baker.ui.activities;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.Recipe;
import com.djdenpa.baker.service.IngredientListWidgetProvider;
import com.djdenpa.baker.ui.fragments.StepDetailsFragment;
import com.djdenpa.baker.ui.fragments.StepListFragment;


/**
 * Created by denpa on 10/29/2017.
 *
 * the activity that shows a recipe expanded
 */

public class RecipeDetailsActivity extends AppCompatActivity implements StepListFragment.OnStepClickListener {

  public static final String RECIPE_EXTRA = "StepListFragment_RECIPE_EXTRA";

  private Recipe mRecipe = null;
  private StepListFragment mStepListFragment;
  private StepDetailsFragment mStepDetailFragment;

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
    mStepDetailFragment = (StepDetailsFragment) fm.findFragmentById(R.id.step_detail_fragment);
    if (mStepDetailFragment != null) {
      mStepDetailFragment.setRecipe(mRecipe);
    }


  }

  @Override
  protected void onStart() {
    if (mStepDetailFragment == null){
      FragmentManager fm = getSupportFragmentManager();
      mStepDetailFragment = (StepDetailsFragment) fm.findFragmentById(R.id.step_detail_fragment);
    }
    super.onStart();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    if (mStepDetailFragment != null) {
      if (isChangingConfigurations() &&
              getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT &&
              mStepDetailFragment.getStepIndex() != -1) {
        OpenDetailActivity(mStepDetailFragment.getStepIndex());
      }
    }

  }

  @Override
  public void onStepClicked(int position) {

    /*
    LESSON TIME FOR ME:
    the reason why fragments need to stay in the fragment manager after orientation change
    is that in case of changing back, the state is preserved.

    So if you want to avoid calling a method on a fragment when it technically got created but is
    not currently on the screen,
    you should just check visibility.
     */
    if (mStepDetailFragment != null && mStepDetailFragment.isVisible()){
      if (!mStepDetailFragment.shouldOpenFullActivity(position)){
        mStepDetailFragment.bindStep(position);
        return;
      }
    }

    //does not have detail fragment
    //load activity
    OpenDetailActivity(position);
  }

  private void OpenDetailActivity( int position){
    Class destinationClass = StepDetailActivity.class;
    Intent intentToStartDetailActivity = new Intent(this, destinationClass);

    intentToStartDetailActivity.putExtra(StepDetailActivity.RECIPE_EXTRA, mRecipe);
    intentToStartDetailActivity.putExtra(StepDetailActivity.STEP_INDEX_EXTRA, position );
    this.startActivity(intentToStartDetailActivity);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_recipe_details, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_save_to_widget) {
      saveCurrentIngredientListToWidget();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public void saveCurrentIngredientListToWidget(){
    SharedPreferences widgetContent = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor = widgetContent.edit();
    editor.putString(Recipe.WIDGET_CONTENT_PREFERENCE_KEY, mRecipe.getIngredientsListString(this));
    editor.putString(Recipe.WIDGET_RECIPE_NAME_PREFERENCE_KEY, mRecipe.name);
    editor.apply();

    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, IngredientListWidgetProvider.class));

    //This function can update GridView, but it does not seem to be able to update a single TextView.
    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);

    Intent updateIntent = new Intent();
    updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
    updateIntent.putExtra(IngredientListWidgetProvider.WIDGET_REFRESH_KEY, appWidgetIds);
    this.sendBroadcast(updateIntent);

    Toast.makeText(this, R.string.save_to_widget_notification,Toast.LENGTH_SHORT).show();
  }

}
