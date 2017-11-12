package com.djdenpa.baker.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.Step;

/**
 * Created by denpa on 11/5/2017.
 */

public class StepDetailFragment extends Fragment {

  private Context mContext;
  private TextView mErrorMessage;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
    mContext = getContext();

    return rootView;
  }


  public void displayError(String errorMessage){
    mErrorMessage.setVisibility(View.VISIBLE);
    mErrorMessage.setText(errorMessage);
  }

  public void bindStep(Step step){

  }
}
