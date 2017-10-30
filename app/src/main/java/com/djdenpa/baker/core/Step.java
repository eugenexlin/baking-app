package com.djdenpa.baker.core;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by denpa on 10/8/2017.
 */

public class Step implements Parcelable {
  public int id;
  public String shortDescription;
  public String description;
  public String videoURL;
  public String thumbnailURL;

  public Step(){
  }


  protected Step(Parcel in) {
    id = in.readInt();
    shortDescription = in.readString();
    description = in.readString();
    videoURL = in.readString();
    thumbnailURL = in.readString();
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeString(shortDescription);
    dest.writeString(description);
    dest.writeString(videoURL);
    dest.writeString(thumbnailURL);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public static final Creator<Step> CREATOR = new Creator<Step>() {
    @Override
    public Step createFromParcel(Parcel in) {
      return new Step(in);
    }

    @Override
    public Step[] newArray(int size) {
      return new Step[size];
    }
  };

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
