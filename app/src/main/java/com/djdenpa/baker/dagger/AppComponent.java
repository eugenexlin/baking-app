package com.djdenpa.baker.dagger;

import com.djdenpa.baker.ui.activities.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by denpa on 12/10/2017.
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
  void inject(MainActivity activity);
}
