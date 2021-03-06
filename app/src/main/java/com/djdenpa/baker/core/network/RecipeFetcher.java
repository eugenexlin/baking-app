package com.djdenpa.baker.core.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.djdenpa.baker.core.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;

/**
 * Created by denpa on 10/7/2017.
 * class to fetch recipe data from network
 */


public class RecipeFetcher {
  public static final String UDACITY_URL = "http://go.udacity.com";
  //private static final String RECIPE_URL = "http://go.udacity.com/android-baking-app-json";

  public static void testGetRecipeList() {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(UDACITY_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build();

    FetchRecipeService getRecipes = retrofit.create(FetchRecipeService.class);

    Call<String> call = getRecipes.getTasks();
    call.enqueue(new Callback<String>() {
      @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
      @Override
      public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
        if (response.isSuccessful()){
          ArrayList<Recipe> result = new ArrayList<>();
          try {
            String responseString = response.body();
            JSONArray jRecipes = new JSONArray(responseString);
            for (int i = 0 ; i < jRecipes.length(); i++) {
              JSONObject jRecipe = jRecipes.getJSONObject(i);
              result.add( Recipe.fromJSON(jRecipe));
            }
          } catch (JSONException e) {
            Log.d("ERROR", e.getMessage());
          }
        }

      }

      @Override
      public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
        Log.d("ERROR", t.getMessage());
      }
    });

  }

  public interface FetchRecipeService {
    @GET("/android-baking-app-json")
    Call<String> getTasks();
  }

}
