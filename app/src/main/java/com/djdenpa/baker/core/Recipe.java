package com.djdenpa.baker.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by denpa on 10/7/2017.
 */

public class Recipe {

  public int id;
  public String name;
  public LinkedList<Ingredient> ingredients;
  public LinkedList<Step> steps;
  public Integer servings;

  //public String image;

  public Recipe(){

  }

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
        JSONObject jStep = (JSONObject) jIngredients.get(index);
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
