package com.djdenpa.baker.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.Ingredient;
import com.djdenpa.baker.core.Recipe;
import com.djdenpa.baker.core.Step;
import com.djdenpa.baker.ui.activities.RecipeDetailsActivity;
import com.djdenpa.baker.ui.adapters.IngredientListItem;
import com.djdenpa.baker.ui.adapters.StepListItem;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by denpa on 10/29/2017.
 *
 * for fast adapter ingredients.
 * contains some checkbox shenanigans.
 */

public class StepListFragment extends Fragment {

  private static final String INGREDIENT_CHECK_STATE = "StepListFragment_INGREDIENT_CHECK_STATE";

  private Context mContext;
  private OnStepClickListener mCallback;
  @BindView(R.id.tv_error_message) TextView mErrorMessage;
  @BindView(R.id.b_to_widget) Button mToWidgetButton;
  private FastItemAdapter<IngredientListItem> mIngredientFastAdapter;
  private FastItemAdapter<StepListItem> mStepFastAdapter;
  @BindView(R.id.rv_ingredients) RecyclerView mRVIngredients;
  @BindView(R.id.rv_steps) RecyclerView mRVSteps;

  private boolean[] mIngredientSelection;

  // OnImageClickListener interface, calls a method in the host activity named onImageSelected
  public interface OnStepClickListener {
    void onStepClicked(int index);
  }

  // Override onAttach to make sure that the container activity has implemented the callback
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    // This makes sure that the host activity has implemented the callback interface
    // If not, it throws an exception
    try {
      mCallback = (OnStepClickListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
              + " must implement OnStepClickListener");
    }
  }

  public StepListFragment() {
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_recipe_details, container, false);
    mContext = getContext();

    ButterKnife.bind(this, rootView );

    final LinearLayoutManager ingredientsLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false){
      @Override
      public boolean canScrollVertically() {
        return false;
      }
    };
    mRVIngredients.setLayoutManager(ingredientsLayoutManager);

    mIngredientFastAdapter = new FastItemAdapter<>();
    //configure our fastAdapter

    mIngredientFastAdapter.withSelectable(true);
    mIngredientFastAdapter.withEventHook(new IngredientListItem.CheckBoxClickEvent());

    mRVIngredients.setAdapter(mIngredientFastAdapter);
    mRVIngredients.setNestedScrollingEnabled(false);

    final LinearLayoutManager stepsLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false){
      @Override
      public boolean canScrollVertically() {
        return false;
      }
    };
    mRVSteps.setLayoutManager(stepsLayoutManager);
    mStepFastAdapter = new FastItemAdapter<>();
    mStepFastAdapter.withOnClickListener(new FastItemAdapter.OnClickListener<StepListItem>() {
      @Override
      public boolean onClick(View v, IAdapter<StepListItem> adapter, StepListItem item, int position) {
        mCallback.onStepClicked(position);
        return true;
      }
    });
    mRVSteps.setAdapter(mStepFastAdapter);
    mRVSteps.setNestedScrollingEnabled(false);

    if(savedInstanceState != null){
      if (savedInstanceState.containsKey(INGREDIENT_CHECK_STATE)){
        mIngredientSelection = savedInstanceState.getBooleanArray(INGREDIENT_CHECK_STATE);
      }
    }

    mToWidgetButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        RecipeDetailsActivity activity = (RecipeDetailsActivity) getActivity();
        if (activity != null){
          activity.saveCurrentIngredientListToWidget();
        }
      }
    });

    return rootView;
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    mIngredientSelection = new boolean[mIngredientFastAdapter.getItemCount()];
    for (int i = 0; i < mIngredientFastAdapter.getItemCount(); i++){
      IngredientListItem ingredientItem = mIngredientFastAdapter.getItem(i);
      mIngredientSelection[i] = ingredientItem.mChecked;
    }
    outState.putBooleanArray(INGREDIENT_CHECK_STATE, mIngredientSelection);

    super.onSaveInstanceState(outState);
  }

  public void displayError(String errorMessage){
    mErrorMessage.setVisibility(View.VISIBLE);
    mErrorMessage.setText(errorMessage);
  }

  public void bindRecipe(Recipe mRecipe) {

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
    if (mIngredientSelection != null){
      for(int i = 0; i < Math.min(ingredients.size(),mIngredientSelection.length); i++){
        ingredients.get(i).mChecked = mIngredientSelection[i];
      }
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

  public Step getStepByPosition(int position){
    return (mStepFastAdapter.getItem(position)).mStep;
  }
}
