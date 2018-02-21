package com.djdenpa.baker.core;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.AnyRes;

/**
 * Created by denpa on 2/20/2018.
 */

public class Utils {

  private static final String[] imageFileExtensions =  new String[] {"jpg", "png", "gif", "jpeg"};

  public static boolean isImage(String path){
    if (path == null){
      return false;
    }
    for (String extension : imageFileExtensions)
    {
      if (path.toLowerCase().endsWith(extension))
      {
        return true;
      }
    }
    return false;
  }

  public static final Uri getUriToDrawable(Context context , @AnyRes int drawableId) {
    Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
            "://" + context.getResources().getResourcePackageName(drawableId)
            + '/' + context.getResources().getResourceTypeName(drawableId)
            + '/' + context.getResources().getResourceEntryName(drawableId) );
    return imageUri;
  }

}
