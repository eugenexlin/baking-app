package com.djdenpa.baker.service;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by denpa on 12/17/2017.
 */

public class RecipeRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

  private Context mContext;
  private List<String> mIngredients = new ArrayList<>();

  public RecipeRemoteViewsFactory(Context applicationContext) {
    mContext = applicationContext;

  }

  @Override
  public void onCreate() {
    pullDataFromPreference();
  }

  @Override
  public void onDataSetChanged() {
    pullDataFromPreference();
  }

  public void pullDataFromPreference(){
    mIngredients.clear();
    SharedPreferences widgetContent = PreferenceManager.getDefaultSharedPreferences(mContext);
    String sIngredients = widgetContent.getString(Recipe.WIDGET_CONTENT_PREFERENCE_KEY,"");
    for( String ingredient : sIngredients.split("\\r?\\n")){
      if (ingredient.trim().length() > 0){
        mIngredients.add(ingredient);
      }
    }
  }

  @Override
  public void onDestroy() {

  }

  @Override
  public int getCount() {
    return mIngredients.size();
  }

  @Override
  public RemoteViews getViewAt(int i) {

    RemoteViews views = new RemoteViews(mContext.getPackageName(),  R.layout.widget_item_ingredient);
    views.setTextViewText(R.id.tv_ingredient, String.valueOf(mIngredients.get(i)));

    return views;
  }

  @Override
  public RemoteViews getLoadingView() {
    return null;
  }

  @Override
  public int getViewTypeCount() {
    return 1;
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public boolean hasStableIds() {
    return true;
  }

}

