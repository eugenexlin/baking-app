package com.djdenpa.baker.core;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by denpa on 10/7/2017.
 */

public class Ingredient {

  /**
   * this will basically be for mapping to some icon if we want cool icons.
   * Other will mean unknown, or "UNIT", or something generic that probably does not need icon.
   */
  enum IngredientMeasureType
  {
    Cup,
    TableSpoon,
    TeaSpoon,
    Other
  }

  public double quantity = 0;
  public String measure = "";
  public IngredientMeasureType measureType = IngredientMeasureType.Other;
  public String ingredient;

  public Ingredient(){
  }

  public static Ingredient fromJSON(JSONObject jIngredient){
    Ingredient result = new Ingredient();
    try {
      result.quantity = jIngredient.getDouble("quantity");
      result.measure = jIngredient.getString("measure");
      result.ingredient = jIngredient.getString("ingredient");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return result;
  }
}
