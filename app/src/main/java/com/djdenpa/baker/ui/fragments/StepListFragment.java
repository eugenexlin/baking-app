package com.djdenpa.baker.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djdenpa.baker.R;

/**
 * Created by denpa on 10/29/2017.
 */

public class StepListFragment extends Fragment {

  Context mContext;

  TextView mErrorMessage;

  public StepListFragment() {
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_recipe_steps, container, false);

    mErrorMessage  = rootView.findViewById(R.id.tv_error_message);

    Debug.stopMethodTracing();

    return rootView;
  }

  public void displayError(String errorMessage){
    mErrorMessage.setVisibility(View.VISIBLE);
    mErrorMessage.setText(errorMessage);
  }
}
