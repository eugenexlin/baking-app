package com.djdenpa.baker.service;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by denpa on 12/17/2017.
 *
 * widget for the ingredients list
 */

public class RecipeWidgetService extends RemoteViewsService {
  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return new RecipeRemoteViewsFactory(this.getApplicationContext());
  }
}
