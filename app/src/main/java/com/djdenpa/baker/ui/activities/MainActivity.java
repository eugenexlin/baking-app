package com.djdenpa.baker.ui.activities;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.BakingApplication;
import com.djdenpa.baker.core.Recipe;
import com.djdenpa.baker.core.network.RecipeFetcher;
import com.djdenpa.baker.idlingresource.MainActivityIdlingResource;
import com.djdenpa.baker.ui.adapters.RecipeListItem;
import com.djdenpa.baker.ui.fragments.RecipeListFragment;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

  MainActivityIdlingResource mIdlingResource;

  RecipeListFragment mRecipeListFragment = null;
  protected BakingApplication mApplication;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mApplication = (BakingApplication) getApplication();

    mApplication.getAppComponent().inject(this);

    setTitle(R.string.recipe_list_header);

    FragmentManager fm = getSupportFragmentManager();
    mRecipeListFragment = (RecipeListFragment) fm.findFragmentById(R.id.master_list_fragment);

    getIdlingResource().setIsIdle(false);
    bindDataOrFetchNew();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_recipe_list, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_randomize) {
      mRecipeListFragment.BindRandomData();
      return true;
    }
    if (id == R.id.action_refresh) {
      fetchDataFromNetwork();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  //call this when starting activity
  public void bindDataOrFetchNew() {
    if (mApplication.mRecipes.size() == 0) {
      fetchDataFromNetwork();
    } else {
      bindRecipesToFragment(mApplication.mRecipes);
      getIdlingResource().setIsIdle(true);
    }
  }

  public void bindRecipesToFragment(List<Recipe> recipes) {

    List<RecipeListItem> dataSource = new ArrayList<>();

    for (Recipe recipe : recipes) {
      dataSource.add(new RecipeListItem(recipe, this));
    }

    mRecipeListFragment.BindData(dataSource);
  }

  public void fetchDataFromNetwork() {

    mRecipeListFragment.showLoadingBar();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(RecipeFetcher.UDACITY_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build();

    RecipeFetcher.FetchRecipeService getRecipes = retrofit.create(RecipeFetcher.FetchRecipeService.class);
    Call<String> call = getRecipes.getTasks();
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          List<Recipe> result = new ArrayList<>();
          try {
            String responseString = response.body();
            JSONArray jRecipes = new JSONArray(responseString);
            for (int i = 0; i < jRecipes.length(); i++) {
              JSONObject jRecipe = jRecipes.getJSONObject(i);
              Recipe recipe = Recipe.fromJSON(jRecipe);
              result.add(recipe);
            }
          } catch (Exception e) {
            Log.d("ERROR", e.getMessage());
          }
          mApplication.mRecipes = result;
          bindRecipesToFragment(result);
        }

        getIdlingResource().setIsIdle(true);
        mRecipeListFragment.hideLoadingBar();
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        Log.d("ERROR", t.getMessage());
        mRecipeListFragment.hideLoadingBar();
      }
    });
  }

  @VisibleForTesting
  @NonNull
  public MainActivityIdlingResource getIdlingResource() {
    if (mIdlingResource == null) {
      mIdlingResource = new MainActivityIdlingResource();
    }
    return mIdlingResource;
  }

}
