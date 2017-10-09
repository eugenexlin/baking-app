package com.djdenpa.baker.core;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by denpa on 10/8/2017.
 */

public class Step {
  public int id;
  public String shortDescription;
  public String description;
  public String videoURL;
  public String thumbnailURL;

  public Step(){
  }


  public static Step fromJSON(JSONObject jStep){
    Step result = new Step();
    try {
      result.id = jStep.getInt("id");
      result.shortDescription = jStep.getString("shortDescription");
      result.description = jStep.getString("description");
      result.videoURL = jStep.getString("videoURL");
      result.thumbnailURL = jStep.getString("thumbnailURL");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return result;
  }
}
