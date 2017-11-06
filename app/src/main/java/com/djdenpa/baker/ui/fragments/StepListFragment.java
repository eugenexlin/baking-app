package com.djdenpa.baker.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.Ingredient;
import com.djdenpa.baker.core.Recipe;
import com.djdenpa.baker.core.Step;
import com.djdenpa.baker.ui.adapters.IngredientListItem;
import com.djdenpa.baker.ui.adapters.StepListItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by denpa on 10/29/2017.
 */

public class StepListFragment extends Fragment {

  private Context mContext;
  private TextView mErrorMessage;
  private FastItemAdapter mIngredientFastAdapter;
  private FastItemAdapter mStepFastAdapter;
  private RecyclerView mRVIngredients;
  private RecyclerView mRVSteps;

  public StepListFragment() {
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_recipe_steps, container, false);
    mContext = getContext();

    mRVIngredients = rootView.findViewById(R.id.rv_ingredients);
    final LinearLayoutManager ingredientsLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false){
      @Override
      public boolean canScrollVertically() {
        return false;
      }
    };
    mRVIngredients.setLayoutManager(ingredientsLayoutManager);
    mIngredientFastAdapter = new FastItemAdapter();
    mRVIngredients.setAdapter(mIngredientFastAdapter);

    mRVSteps = rootView.findViewById(R.id.rv_steps);
    final LinearLayoutManager stepsLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false){
      @Override
      public boolean canScrollVertically() {
        return false;
      }
    };
    mRVSteps.setLayoutManager(stepsLayoutManager);
    mStepFastAdapter = new FastItemAdapter();
    mRVSteps.setAdapter(mStepFastAdapter);



    mErrorMessage  = rootView.findViewById(R.id.tv_error_message);

    return rootView;
  }

  public void displayError(String errorMessage){
    mErrorMessage.setVisibility(View.VISIBLE);
    mErrorMessage.setText(errorMessage);
  }

  public void BindRecipe(Recipe mRecipe) {

    //bind ingredients
    mIngredientFastAdapter.clear();
    List<IngredientListItem> ingredients = new ArrayList<>();
    try {
      for (Ingredient ingredient : mRecipe.ingredients()) {
        ingredients.add(new IngredientListItem(ingredient, mContext));
      }
    } catch (Exception e) {
      Log.d("ERROR", e.getMessage());
    }
    mIngredientFastAdapter.add(ingredients);

    //bind steps
    mStepFastAdapter.clear();
    List<StepListItem> steps = new ArrayList<>();
    try {
      for (Step step : mRecipe.steps()) {
        steps.add(new StepListItem(step, mContext));
      }
    } catch (Exception e) {
      Log.d("ERROR", e.getMessage());
    }
    mStepFastAdapter.add(steps);


  }
}
