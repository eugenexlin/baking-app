package com.djdenpa.baker.core;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by denpa on 10/7/2017.
 */

public class Recipe implements Parcelable {

  public int id;
  public String name;
  public LinkedList<Ingredient> ingredients;
  public LinkedList<Step> steps;
  public Integer servings;

  //public String image;

  public Recipe(){

  }

  protected Recipe(Parcel in) {
    id = in.readInt();
    name = in.readString();
    ingredients = new LinkedList<>();
    in.readTypedList(ingredients, Ingredient.CREATOR);
    steps = new LinkedList<>();
    in.readTypedList(steps, Step.CREATOR);
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
    parcel.writeList(ingredients);
    parcel.writeList(steps);
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
      JSONArray jIngredients = jRecipe.getJSONArray("ingredients");
      result.ingredients = new LinkedList<>();
      for (int index = 0; index < jIngredients.length(); index++){
        JSONObject jIngredient = (JSONObject) jIngredients.get(index);
        result.ingredients.add(Ingredient.fromJSON(jIngredient));
      }
      JSONArray jSteps = jRecipe.getJSONArray("steps");
      result.steps  = new LinkedList<>();
      for (int index = 0; index < jSteps.length(); index++){
        JSONObject jStep = (JSONObject) jSteps.get(index);
        result.steps.add(Step.fromJSON(jStep));
      }
      result.servings = jRecipe.getInt("servings");
      //result.image
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return result;
  }
}
