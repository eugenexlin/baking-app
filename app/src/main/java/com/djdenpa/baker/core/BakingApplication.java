package com.djdenpa.baker.core;

import android.app.Application;

import com.djdenpa.baker.dagger.AppComponent;
import com.djdenpa.baker.dagger.AppModule;
import com.djdenpa.baker.dagger.DaggerAppComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by denpa on 12/10/2017.
 *
 * This will server as the single state type model
 */

public class BakingApplication extends Application {

  //store recipes on application for easier testing.
  public List<Recipe> mRecipes = new ArrayList<>();

  private AppComponent appComponent;

  public AppComponent getAppComponent() {
    return appComponent;
  }

  private AppComponent initDagger(BakingApplication application) {
    return DaggerAppComponent.builder()
            .appModule(new AppModule(application))
            .build();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    appComponent = initDagger(this);
  }

}
