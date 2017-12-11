package com.djdenpa.baker.core;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by denpa on 10/7/2017.
 */

public class Ingredient implements Parcelable {


  protected Ingredient(Parcel in) {
    quantity = in.readDouble();
    measure = in.readString();
    ingredient = in.readString();
  }

  public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
    @Override
    public Ingredient createFromParcel(Parcel in) {
      return new Ingredient(in);
    }

    @Override
    public Ingredient[] newArray(int size) {
      return new Ingredient[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeDouble(quantity);
    parcel.writeString(measure);
    parcel.writeString(ingredient);
  }

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

  public String readableQuantity(){
    //first some known values??
    //perhaps this can be replaced with a smart fraction converter someday.
    //this should only be done for small stuff like cups and spoons.
    //so values less than like 10
    if (quantity < 10){
      if (areAboutEqual(quantity, 0.5)){
        return "1/2";
      }
      if (areAboutEqual(quantity, 1.5)){
        return "1 1/2";
      }
      if (areAboutEqual(quantity, 2.5)){
        return "2 1/2";
      }
      if (areAboutEqual(quantity, 0.25)){
        return "1/4";
      }
      if (areAboutEqual(quantity, 0.75)){
        return "3/4";
      }
      if (areAboutEqual(quantity, 1.25)){
        return "1 1/4";
      }
      if (areAboutEqual(quantity, 1.75)){
        return "1 3/4";
      }
      if (areAboutEqual(quantity, 2.25)){
        return "2 1/4";
      }
      if (areAboutEqual(quantity, 2.75)){
        return "2 3/4";
      }
      if (areAboutEqual(quantity, 0.3333)){
        return "1/3";
      }
      if (areAboutEqual(quantity, 0.6666)){
        return "2/3";
      }
      if (areAboutEqual(quantity, 1.3333)){
        return "1 1/3";
      }
      if (areAboutEqual(quantity, 1.6666)){
        return "1 2/3";
      }
      if (areAboutEqual(quantity, 2.3333)){
        return "2 1/3";
      }
      if (areAboutEqual(quantity, 2.6666)){
        return "2 2/3";
      }
    }

    if (noSignificantDecimals(quantity)){
      return String.valueOf((int)quantity);
    }

    return String.valueOf(quantity);
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

  public static JSONObject toJSON(Ingredient ingerdient){
    JSONObject jStep = new JSONObject();
    try {
      jStep.put("quantity", ingerdient.quantity);
      jStep.put("measure", ingerdient.measure);
      jStep.put("ingredient", ingerdient.ingredient);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jStep;
  }

  //compare numbers
  private boolean areAboutEqual(double a, double b){
    return (Math.abs(a - b) < 0.01);
  }
  //compare numbers
  private boolean noSignificantDecimals(double a){
    return (((int)(a*10)) % 10) == 0;
  }
}
