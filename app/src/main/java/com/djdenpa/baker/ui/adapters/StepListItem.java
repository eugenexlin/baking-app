package com.djdenpa.baker.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.Step;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by denpa on 11/5/2017.
 * step item for the fast adapter
 */

public class StepListItem extends AbstractItem<StepListItem, StepListItem.ViewHolder> {
  public final Step mStep;

  public StepListItem(Step step, Context context){
    mStep = step;
  }

  @Override
  public StepListItem.ViewHolder getViewHolder(View v) {
    return new StepListItem.ViewHolder(v);
  }

  @Override
  public int getType() {
    return 0;
  }

  @Override
  public int getLayoutRes() {
    return R.layout.item_recipe_step;
  }


  //The logic to bind your data to the view
  @Override
  public void bindView(StepListItem.ViewHolder viewHolder, List<Object> payloads) {
    //call super so the selection is already handled for you
    super.bindView(viewHolder, payloads);

    viewHolder.mTVStep.setText(mStep.shortDescription);
    viewHolder.mTVDescription.setText(mStep.description);
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    final TextView mTVStep;
    final TextView mTVDescription;

    ViewHolder(View view) {
      super(view);
      this.mTVStep = view.findViewById(R.id.tv_step);
      this.mTVDescription = view.findViewById(R.id.tv_long_description);
    }
  }
}
