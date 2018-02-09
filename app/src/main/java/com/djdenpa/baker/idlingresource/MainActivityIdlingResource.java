package com.djdenpa.baker.idlingresource;

import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by denpa on 12/11/2017.
 * from udacity lesson
 */

public class MainActivityIdlingResource implements IdlingResource {

  @Nullable
  private volatile ResourceCallback mCallback;

  private final AtomicBoolean mIsIdle = new AtomicBoolean(true);

  @Override
  public String getName() {
    return this.getClass().getName();
  }

  @Override
  public boolean isIdleNow() {
    return mIsIdle.get();
  }

  @Override
  public void registerIdleTransitionCallback(ResourceCallback callback) {
    mCallback = callback;
  }

  public void setIsIdle(boolean isIdle) {
    mIsIdle.set(isIdle);
    if (isIdle) {
      ResourceCallback callbackSnapshot = mCallback;
      if (callbackSnapshot != null) {
        callbackSnapshot.onTransitionToIdle();
      }
    }
  }
}
