package com.djdenpa.baker.service;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.Recipe;
import com.djdenpa.baker.ui.activities.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientListWidgetProvider extends AppWidgetProvider {

  static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                              int appWidgetId) {
    RemoteViews rv = getIngredientsRemoteView(context);
    appWidgetManager.updateAppWidget(appWidgetId, rv);
  }

  /**
   * Creates and returns the RemoteViews to be displayed in the GridView mode widget
   *
   * @param context The context
   * @return The RemoteViews for the GridView mode widget
   */
  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  private static RemoteViews getIngredientsRemoteView(Context context) {
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_list_widget);

    // Set the GridWidgetService intent to act as the adapter for the GridView
    Intent intent = new Intent(context, RecipeWidgetService.class);
    views.setRemoteAdapter(R.id.widget_list_view, intent);

    Intent StartActivityIntent = new Intent(context, MainActivity.class);
    PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, StartActivityIntent, 0);
    views.setOnClickPendingIntent(R.id.ll_widget,configPendingIntent);

    // Set the widget title template
    SharedPreferences widgetContent = PreferenceManager.getDefaultSharedPreferences(context);
    String sIngredientName = widgetContent.getString(Recipe.WIDGET_RECIPE_NAME_PREFERENCE_KEY, "");

    if (sIngredientName.length() > 0) {
      String sIngredientHeaderTemplate = context.getString(R.string.widget_ingredient_header_template);
      views.setTextViewText(R.id.tv_widget_header, String.format(sIngredientHeaderTemplate, sIngredientName));
      views.setViewVisibility(R.id.tv_widget_header, View.VISIBLE);
      views.setViewVisibility(R.id.widget_list_view, View.VISIBLE);
      views.setViewVisibility(R.id.empty_view, View.GONE);
    }

    return views;
  }


  public static final String WIDGET_REFRESH_KEY ="DJ_DENPA_BAKER_WIDGET_REFRESH_KEY";

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.hasExtra(WIDGET_REFRESH_KEY)) {
      int[] ids = intent.getExtras().getIntArray(WIDGET_REFRESH_KEY);
      this.onUpdate(context, AppWidgetManager.getInstance(context), ids);
    } else super.onReceive(context, intent);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    // There may be multiple widgets active, so update all of them
    for (int appWidgetId : appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId);
    }
  }

  @Override
  public void onEnabled(Context context) {
    // Enter relevant functionality for when the first widget is created
  }

  @Override
  public void onDisabled(Context context) {
    // Enter relevant functionality for when the last widget is disabled
  }
}

