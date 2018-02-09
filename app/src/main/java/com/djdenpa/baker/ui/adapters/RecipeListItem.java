package com.djdenpa.baker.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.Recipe;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by denpa on 10/15/2017.
 *
 * Recipe item for the fast adapter
 */

public class RecipeListItem extends AbstractItem<RecipeListItem, RecipeListItem.ViewHolder> {
  public final Recipe mRecipe;
  //public Context mContext;
  private final String servingsHeader;

  public RecipeListItem(Recipe recipe, Context context){
    //SAVE RESOURCES INSTEAD OF CONTEXT FOR SPED OPTIMIZATION
    //mContext = context;
    mRecipe = recipe;
    servingsHeader = context.getString(R.string.recipe_servings_header);
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
    viewHolder.mServings.setText(String.format(servingsHeader, mRecipe.servings) );
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    final TextView mName;
    final TextView mServings;

    ViewHolder(View view) {
      super(view);
      this.mName = view.findViewById(R.id.tv_recipe_name);
      this.mServings = view.findViewById(R.id.tv_servings);
    }
  }
}