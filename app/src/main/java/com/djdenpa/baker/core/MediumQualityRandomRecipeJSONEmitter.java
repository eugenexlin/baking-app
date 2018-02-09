package com.djdenpa.baker.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by denpa on 10/29/2017.
 *
 * This class is just for creating random test data to see how the UI will look.
 */

public class MediumQualityRandomRecipeJSONEmitter {

  private static final Random rand = new Random();

  public static JSONArray GenerateSomeLongRecipeList(){
    JSONArray jRecipes = new JSONArray();

    int recipeNum = rand.nextInt(50);

    for (int i = 1; i <= recipeNum; i++){
      jRecipes.put(GenerateOneRecipe(i));
    }

    return jRecipes;
  }

  private static JSONObject GenerateOneRecipe(int id){

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


  private static JSONArray GenerateSomeLongStepList(){
    JSONArray jSteps = new JSONArray();

    int recipeNum = rand.nextInt(25) + 5;

    for (int i = 1; i <= recipeNum; i++){
      jSteps.put(GenerateOneStep(i));
    }

    return jSteps;
  }

  private static JSONObject GenerateOneStep(int id){

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

  private static JSONArray GenerateSomeLongIngredientList(){
    JSONArray jIngredients = new JSONArray();

    int recipeNum = rand.nextInt(10) + 5;

    for (int i = 1; i <= recipeNum; i++){
      jIngredients.put(GenerateOneIngredient());
    }

    return jIngredients;
  }

  private static JSONObject GenerateOneIngredient(){

    JSONObject jRecipe = new JSONObject();

    try {
      jRecipe.put("measure", units[rand.nextInt(units.length)]);

      double quantity;
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

  private static final String[] units = {"TSP", "TBLSP", "CUP", "G", "OZ", "K", "UNIT" };


  //look at this sweet character distribution
  private static final String vowelDistribution = "aaaaaeeeeeeiiioooouuy";
  private static final String consonantDistribution = "bbbcdddfffggghhhjjkkkllmmmmnnnnppqrrssssstttttvwxz";

  private static final String charactersThatOftenDouble = "eoblt";

  /**
   * uses arbitrary rules to generate a random phrase
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
        currentChar = SelectAChar(consonantDistribution, currentChar);
      }else{
        currentChar = SelectAChar(vowelDistribution, currentChar);
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

  private static char SelectAChar(String inputString, char notThisChar){
    char selectedChar;
    do{
      int index = rand.nextInt(inputString.length());
      selectedChar =  inputString.charAt(index);
    }while(selectedChar == notThisChar);
    return selectedChar;
  }
}
