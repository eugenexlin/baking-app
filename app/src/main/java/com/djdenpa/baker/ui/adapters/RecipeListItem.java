package com.djdenpa.baker.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.Recipe;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by denpa on 10/15/2017.
 */

public class RecipeListItem extends AbstractItem<RecipeListItem, RecipeListItem.ViewHolder> {
  public Recipe mRecipe;

  public RecipeListItem(Recipe recipe){
    mRecipe = recipe;
  }

  @Override
  public RecipeListItem.ViewHolder getViewHolder(View v) {
    return new ViewHolder(v);
  }

  @Override
  public int getType() {
    return 0;
  }

  @Override
  public int getLayoutRes() {
    return R.layout.item_recipe_list;
  }

  //The logic to bind your data to the view
  @Override
  public void bindView(ViewHolder viewHolder, List<Object> payloads) {
    //call super so the selection is already handled for you
    super.bindView(viewHolder, payloads);

    viewHolder.mName.setText(mRecipe.name);
  }

  protected static class ViewHolder extends RecyclerView.ViewHolder {
    protected TextView mName;
    protected TextView description;

    public ViewHolder(View view) {
      super(view);
      this.mName = view.findViewById(R.id.tv_recipe_name);
    }
  }
}