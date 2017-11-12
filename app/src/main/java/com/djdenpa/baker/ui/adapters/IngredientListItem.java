package com.djdenpa.baker.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.Ingredient;
import com.djdenpa.baker.core.Recipe;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by denpa on 11/5/2017.
 */

public class IngredientListItem extends AbstractItem<IngredientListItem, IngredientListItem.ViewHolder> {
  public Ingredient mIngredient;
  public String mIngredientTextTemplate;
  public String mIngredientMeasureTemplate;

  public IngredientListItem(Ingredient recipe, Context context){
    mIngredient = recipe;
    mIngredientTextTemplate = context.getString(R.string.ingredient_template);

    boolean canBePlural = true;

    switch(mIngredient.measure.toUpperCase()){
      case "CUP":
        mIngredientMeasureTemplate = context.getString(R.string.measure_text_cup);
        break;
      case "TSP":
        mIngredientMeasureTemplate = context.getString(R.string.measure_text_teaspoon);
        break;
      case "TBLSP":
        mIngredientMeasureTemplate = context.getString(R.string.measure_text_tablespoon);
        break;
      case "G":
        mIngredientMeasureTemplate = context.getString(R.string.measure_text_gram);
        canBePlural = false;
        break;
      case "OZ":
        mIngredientMeasureTemplate = context.getString(R.string.measure_text_ounce);
        canBePlural = false;
        break;
      case "UNIT":
      case "K":
        mIngredientMeasureTemplate = context.getString(R.string.measure_text_unit);
        canBePlural = false;
        break;
      default:
        mIngredientMeasureTemplate = context.getString(R.string.measure_text_unknown);
        canBePlural = false;
        break;
    }

    //CHECK IF NEEDS "s"
    //this should probably be only for locales that like just adding s at the end..
    if (canBePlural && mIngredient.quantity != 1){
      mIngredientMeasureTemplate = mIngredientMeasureTemplate + "s";
    }
  }

  @Override
  public ViewHolder getViewHolder(View v) {
    return new ViewHolder(v);
  }

  @Override
  public int getType() {
    return 0;
  }

  @Override
  public int getLayoutRes() {
    return R.layout.item_recipe_ingredient;
  }


  //The logic to bind your data to the view
  @Override
  public void bindView(ViewHolder viewHolder, List<Object> payloads) {
    //call super so the selection is already handled for you
    super.bindView(viewHolder, payloads);

    String measureText = String.format(mIngredientMeasureTemplate, mIngredient.readableQuantity(), mIngredient.measure.toLowerCase());
    viewHolder.mCBIngredient.setText(String.format(mIngredientTextTemplate, measureText, mIngredient.ingredient));
  }

  protected static class ViewHolder extends RecyclerView.ViewHolder {
    protected CheckBox mCBIngredient;

    public ViewHolder(View view) {
      super(view);
      this.mCBIngredient = view.findViewById(R.id.cb_ingredient);
    }
  }
}
