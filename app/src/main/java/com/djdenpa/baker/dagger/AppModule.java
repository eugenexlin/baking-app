package com.djdenpa.baker.dagger;

import android.content.Context;

import com.djdenpa.baker.core.BakingApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by denpa on 12/10/2017.
 *
 */

@Module
public class AppModule {

  private final BakingApplication application;

  public AppModule(BakingApplication application) {
    this.application = application;
  }

  @Provides
  @Singleton
  Context provideContext() {
    return application;
  }

}
