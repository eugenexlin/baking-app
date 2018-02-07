package com.djdenpa.baker.core;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.djdenpa.baker.R;
import com.djdenpa.baker.service.IngredientListWidgetProvider;
import com.djdenpa.baker.ui.adapters.IngredientListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by denpa on 10/7/2017.
 */

public class Recipe implements Parcelable {

  public static final String WIDGET_CONTENT_PREFERENCE_KEY = "WIDGET_CONTENT_PREFERENCE_KEY";
  public static final String WIDGET_RECIPE_NAME_PREFERENCE_KEY = "WIDGET_RECIPE_NAME_PREFERENCE_KEY";

  public int id;
  public String name;
  public String ingredientsJSON;
  private LinkedList<Ingredient> mIngredients = null;
  public LinkedList<Ingredient> ingredients(){
    if (mIngredients == null) {
      mIngredients = new LinkedList<>();
      JSONArray jIngredients;
      try {
        jIngredients = new JSONArray(ingredientsJSON);
        for (int index = 0; index < jIngredients.length(); index++) {
          JSONObject jIngredient = (JSONObject) jIngredients.get(index);
          mIngredients.add(Ingredient.fromJSON(jIngredient));
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return mIngredients;
  }
  /*
  pass context so we get the string templates.
   */
  public String getIngredientsListString(Context context){
    StringBuilder sb = new StringBuilder();
    for (Ingredient ingredient : ingredients()) {
      IngredientListItem ili = new IngredientListItem(ingredient, context);
      sb.append(ili.getDisplayText());
      sb.append("\n");
    }
    return sb.toString();
  }

  public String stepsJSON;
  private LinkedList<Step> mSteps = null;
  public LinkedList<Step> steps(){
    if (mSteps == null) {
      mSteps = new LinkedList<>();
      JSONArray jSteps;
      try {
        jSteps = new JSONArray(stepsJSON);
        for (int index = 0; index < jSteps.length(); index++) {
          JSONObject jIngredient = (JSONObject) jSteps.get(index);
          mSteps.add(Step.fromJSON(jIngredient));
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return mSteps;
  }
  public Integer servings;

  //public String image;

  public Recipe(){

  }

  protected Recipe(Parcel in) {
    id = in.readInt();
    name = in.readString();
    ingredientsJSON = in.readString();
    stepsJSON = in.readString();
    servings = in.readInt();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeInt(id);
    parcel.writeString(name);
    parcel.writeString(ingredientsJSON);
    parcel.writeString(stepsJSON);
    parcel.writeInt(servings);
  }

  public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
    @Override
    public Recipe createFromParcel(Parcel in) {
      return new Recipe(in);
    }

    @Override
    public Recipe[] newArray(int size) {
      return new Recipe[size];
    }
  };

  public static Recipe fromJSON(JSONObject jRecipe){
    Recipe result = new Recipe();
    try {
      result.id = jRecipe.getInt("id");
      result.name = jRecipe.getString("name");
      result.ingredientsJSON = jRecipe.getString("ingredients");
      result.stepsJSON = jRecipe.getString("steps");
      result.servings = jRecipe.getInt("servings");
      //result.image
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return result;
  }

}
