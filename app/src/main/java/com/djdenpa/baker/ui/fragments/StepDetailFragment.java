package com.djdenpa.baker.ui.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.Step;

/**
 * Created by denpa on 11/5/2017.
 */

public class StepDetailFragment extends Fragment {

  private Context mContext;
  private TextView mErrorMessage;
  private  FrameLayout mExoPlayerFrameLayout;
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    final View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
    mContext = getContext();


    mExoPlayerFrameLayout = rootView.findViewById(R.id.fl_exo_player);
    //this will try to adapt the height of the video player to aspect ratio of the used screen space.

    rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        double fragmentWidth = (float) mExoPlayerFrameLayout.getWidth();
        //set the dimensions to 16:9 regardless of screen size.
        int heightToSet = (int)Math.floor(fragmentWidth/16.0*9.0);
        mExoPlayerFrameLayout.getLayoutParams().height = heightToSet;
        mExoPlayerFrameLayout.requestLayout();

        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
      }
    });



    return rootView;
  }


  public void displayError(String errorMessage){
    mErrorMessage.setVisibility(View.VISIBLE);
    mErrorMessage.setText(errorMessage);
  }

  public void bindStep(Step step){

  }
}
