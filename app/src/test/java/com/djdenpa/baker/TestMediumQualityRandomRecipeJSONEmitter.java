package com.djdenpa.baker;

import com.djdenpa.baker.core.MediumQualityRandomRecipeJSONEmitter;

import org.junit.Test;

/**
 * Created by denpa on 10/29/2017.
 *
 * just for fun maybe
 */

public class TestMediumQualityRandomRecipeJSONEmitter {
  @Test
  public void testSomeGeneration() throws Exception {


    System.out.println(MediumQualityRandomRecipeJSONEmitter.GenerateStringWord(true));
    System.out.println(MediumQualityRandomRecipeJSONEmitter.GenerateStringWord(false));

    System.out.println("--");

    System.out.println(MediumQualityRandomRecipeJSONEmitter.GenerateStringPhrase(3,true));
    System.out.println(MediumQualityRandomRecipeJSONEmitter.GenerateStringPhrase(3,true));
    System.out.println(MediumQualityRandomRecipeJSONEmitter.GenerateStringPhrase(3,true));



    System.out.println(MediumQualityRandomRecipeJSONEmitter.GenerateStringPhrase(30,false));

  }
}
