package com.djdenpa.baker.core;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by denpa on 10/29/2017.
 */

public class MediumQualityRandomRecipeJSONEmitter {

  public static Random rand = new Random();

  public static JSONArray GenerateSomeLongRecipeList(){
    JSONArray jRecipes = new JSONArray();

    int recipeNum = rand.nextInt(50);

    for (int i = 1; i <= recipeNum; i++){
      jRecipes.put(GenerateOneRecipe(i));
    }

    return jRecipes;
  }

  public static JSONObject GenerateOneRecipe(int id){

    JSONObject jRecipe = new JSONObject();

    try {
      jRecipe.put("id",id);
      jRecipe.put("name", GenerateStringPhrase(3, true));

      jRecipe.put("ingredients", GenerateSomeLongIngredientList());
      jRecipe.put("steps", GenerateSomeLongStepList());

      jRecipe.put("servings", (1 + rand.nextInt(4))* (1 + rand.nextInt(4)));
      jRecipe.put("image", "");

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return jRecipe;
  }


  public static JSONArray GenerateSomeLongStepList(){
    JSONArray jSteps = new JSONArray();

    int recipeNum = rand.nextInt(25) + 5;

    for (int i = 1; i <= recipeNum; i++){
      jSteps.put(GenerateOneStep(i));
    }

    return jSteps;
  }

  public static JSONObject GenerateOneStep(int id){

    JSONObject jStep = new JSONObject();

    try {
      jStep.put("id",id);
      jStep.put("shortDescription", GenerateStringPhrase(7, false));
      jStep.put("description", GenerateStringPhrase(50, false));

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return jStep;
  }

  public static JSONArray GenerateSomeLongIngredientList(){
    JSONArray jIngredients = new JSONArray();

    int recipeNum = rand.nextInt(10) + 5;

    for (int i = 1; i <= recipeNum; i++){
      jIngredients.put(GenerateOneIngredient());
    }

    return jIngredients;
  }

  public static JSONObject GenerateOneIngredient(){

    JSONObject jRecipe = new JSONObject();

    try {
      jRecipe.put("measure", units[rand.nextInt(units.length)]);

      double quantity = 0;
      if (rand.nextInt(10) < 5){
        quantity = rand.nextInt(5);
      }else{

        if (rand.nextInt(10) < 5){
          quantity =0.25 * rand.nextInt(12);
        }else{

          if (rand.nextInt(10) < 5){
            quantity = rand.nextInt(1000);
            jRecipe.put("measure", "G");
          }else{

            quantity =0.3333 * rand.nextInt(9);

          }
        }
      }

      if (quantity <= 0){
        quantity = 1 + rand.nextInt(9);
      }

      jRecipe.put("quantity", quantity);
      jRecipe.put("ingredient", GenerateStringPhrase(3, false));


    } catch (JSONException e) {
      e.printStackTrace();
    }

    return jRecipe;
  }

  public static String[] units = {"TSP", "TBLSP", "CUP", "G", "OZ", "K", "UNIT" };


  //look at this sweet character distribution
  public static String vowely = "aaaaaeeeeeeiiioooouuy";
  public static String consonanty = "bbbcdddfffggghhhjjkkkllmmmmnnnnppqrrssssstttttvwxz";

  public static String charactersThatOftenDouble = "eoblt";

  /**
   *
   * @param approxMaxWords
   * @param properCasing true = every one uppercased, false = only first
   * @return
   */
  public static String GenerateStringPhrase(int approxMaxWords, boolean properCasing){
    int numberOfWords = rand.nextInt(approxMaxWords) + approxMaxWords /2;
    if (numberOfWords < 1){
      numberOfWords = 1;
    }

    boolean shouldUppercase = true;
    StringBuilder output = new StringBuilder();
    int punctuationMeter = 0;

    for (int i = 1; i <= numberOfWords; i++){
      String word = GenerateStringWord(shouldUppercase);

      if (!properCasing){
        shouldUppercase = false;
      }

      output.append(word);
      punctuationMeter += 1;
      if (i < numberOfWords){
        if (punctuationMeter < 5){
          output.append(" ");
        }else{
          switch(rand.nextInt(6)){
            case 0:
              output.append(", ");
              punctuationMeter = 0;
              break;
            case 1:
              output.append(". ");
              shouldUppercase = true;
              punctuationMeter = 0;
              break;
            default:
              output.append(" ");
          }
        }
      }
    }

    if (numberOfWords > 4) {
      output.append(".");
    }

   return output.toString();
  }


  public static String GenerateStringWord(boolean shouldUppercase){
    boolean firstCharCaps = shouldUppercase;
    StringBuilder output = new StringBuilder();
    boolean nextIsConsonant = (rand.nextInt(10) < 7);

    char currentChar = 0;

    //adding and subtracting randoms makes it tend to average value instead of linear spread.
    int wordLength = 5 - rand.nextInt(3) - rand.nextInt(3) - rand.nextInt(3) + rand.nextInt(3) + rand.nextInt(3) + rand.nextInt(3);
    if (wordLength <= 1){
      wordLength = 1;
      nextIsConsonant = false;
    }

    //the higher it is, the more change to switch from consonant to vowel
    int chanceToSwitch = 0;

    for (int i = 1; i <= wordLength; i++){
      if (nextIsConsonant) {
        currentChar = SelectAChar(consonanty, currentChar);
      }else{
        currentChar = SelectAChar(vowely, currentChar);
      }

      if (firstCharCaps) {
        output.append((""+currentChar).toUpperCase());
        firstCharCaps = false;
      }else{
        output.append(currentChar);
      }

      //check if we should double up the char

      //first letter probably should not double
      if(i > 1) {

        //check if is a double-able letter
        if (charactersThatOftenDouble.indexOf(currentChar) >= 0) {

          //we more likely only want to double if it is just changed from consonant to vowel,
          // and vice versa
          if (chanceToSwitch == 0) {
            //have a fairly rare double chance.
            if (rand.nextInt(20) < 3) {
              output.append(currentChar);
              chanceToSwitch++;
            }
          }
        }
      }

      //now we logic for if the word will change.
      chanceToSwitch++;
      boolean switchMe = false;
      if (nextIsConsonant) {
        if ((rand.nextInt(3)-rand.nextInt(3)) < chanceToSwitch){
          switchMe = true;
        }
      }else{
        if ((rand.nextInt(2)-rand.nextInt(2)) < chanceToSwitch){
          switchMe = true;
        }
      }
      if (switchMe){
        nextIsConsonant = !nextIsConsonant;
        chanceToSwitch = 0;
      }
    }


    return output.toString();

  }

  public static char SelectAChar(String inputString, char notThisChar){
    char selectedChar = 0;
    do{
      int index = rand.nextInt(inputString.length());
      selectedChar =  inputString.charAt(index);
    }while(selectedChar == notThisChar);
    return selectedChar;
  }
}
