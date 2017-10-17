package com.djdenpa.baker.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.Recipe;
import com.djdenpa.baker.core.network.RecipeFetcher;
import com.djdenpa.baker.ui.adapters.RecipeListItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

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

/**
 * Created by denpa on 9/23/2017.
 */

public class RecipeListFragment extends Fragment {

  Context mContext;
  RecyclerView mRVRecipes;

  public RecipeListFragment() {
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);
    mContext = getContext();

    mRVRecipes = rootView.findViewById(R.id.rv_recipe_list);

    final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
    mRVRecipes.setLayoutManager(layoutManager);

    final FastItemAdapter fastAdapter = new FastItemAdapter();
    mRVRecipes.setAdapter(fastAdapter);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(RecipeFetcher.UDACITY_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build();

    RecipeFetcher.FetchRecipeService getRecipes = retrofit.create(RecipeFetcher.FetchRecipeService.class);
    Call<String> call = getRecipes.getTasks();
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()){
          List<RecipeListItem> result = new ArrayList<>();
          try {
            String responseString = response.body();
            JSONArray jRecipes = new JSONArray(responseString);
            for (int i = 0 ; i < jRecipes.length(); i++) {
              JSONObject jRecipe = jRecipes.getJSONObject(i);
              Recipe recipe = Recipe.fromJSON(jRecipe);
              result.add(new RecipeListItem(recipe));
              //result.add( new RecipeListItem(Recipe.fromJSON(jRecipe)));
            }
          } catch (Exception e) {
            Log.d("ERROR", e.getMessage());
          }

          fastAdapter.add(result);
        }

      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        Log.d("ERROR", t.getMessage());
      }
    });


    return rootView;

  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }
}
