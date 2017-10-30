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
