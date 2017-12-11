package com.djdenpa.baker.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.djdenpa.baker.R;
import com.djdenpa.baker.ui.activities.RecipeStepsActivity;
import com.djdenpa.baker.core.MediumQualityRandomRecipeJSONEmitter;
import com.djdenpa.baker.core.Recipe;
import com.djdenpa.baker.core.network.RecipeFetcher;
import com.djdenpa.baker.ui.adapters.RecipeListItem;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by denpa on 9/23/2017.
 */

public class RecipeListFragment extends Fragment {

  Context mContext;
  RecyclerView mRVRecipes;
  FastItemAdapter mFastAdapter;
  ProgressBar mPBLoading;

  TextView mErrorMessage;


  public RecipeListFragment() {
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);
    mContext = getContext();

    mRVRecipes = rootView.findViewById(R.id.rv_recipe_list);
    mPBLoading = rootView.findViewById(R.id.pb_loading);

    mErrorMessage  = rootView.findViewById(R.id.tv_error_message);

    final GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, calculateNoOfColumns(mContext), GridLayoutManager.VERTICAL ,false );
    mRVRecipes.setLayoutManager(gridLayoutManager);

    mFastAdapter = new FastItemAdapter();
    mFastAdapter.withSelectable(true);
    mFastAdapter.withOnClickListener(new FastItemAdapter.OnClickListener() {
      @Override
      public boolean onClick(View v, IAdapter adapter, IItem item, int position) {
        Recipe selectedRecipe = ((RecipeListItem) mFastAdapter.getAdapterItem(position)).mRecipe;

        Class destinationClass = RecipeStepsActivity.class;
        Intent intentToStartDetailActivity = new Intent(mContext, destinationClass);

        intentToStartDetailActivity.putExtra(RecipeStepsActivity.RECIPE_EXTRA, selectedRecipe);
        mContext.startActivity(intentToStartDetailActivity);

        return true;
      }
    });

    mRVRecipes.setAdapter(mFastAdapter);

    showLoadingBar();

    return rootView;

  }

  public void showLoadingBar(){
    mPBLoading.setVisibility(View.VISIBLE);
  }
  public void hideLoadingBar(){
    mPBLoading.setVisibility(View.GONE);
  }

  private static int calculateNoOfColumns(Context context) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    //here is a magical formula to calculate number of columns we should display
    float interestingWidth = 500 + (displayMetrics.widthPixels * 2) / (displayMetrics.density + 2.5f);
    int scalingFactor = 400;
    int noOfColumns = (int) (interestingWidth / scalingFactor);
    if (noOfColumns < 2){
      noOfColumns = 2;
    }
    return noOfColumns;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    return;
  }

  public void BindData(List<RecipeListItem> data){
    mFastAdapter.clear();
    mFastAdapter.add(data);
  }

  public void BindRandomData() {
    mFastAdapter.clear();

    showLoadingBar();

    JSONArray jRecipes = MediumQualityRandomRecipeJSONEmitter.GenerateSomeLongRecipeList();
    List<RecipeListItem> result = new ArrayList<>();
    try {
      for (int i = 0 ; i < jRecipes.length(); i++) {
        JSONObject jRecipe = jRecipes.getJSONObject(i);
        Recipe recipe = Recipe.fromJSON(jRecipe);
        result.add(new RecipeListItem(recipe, mContext));
      }
    } catch (Exception e) {
      Log.d("ERROR", e.getMessage());
    }

    mFastAdapter.add(result);

    hideLoadingBar();
  }

}
