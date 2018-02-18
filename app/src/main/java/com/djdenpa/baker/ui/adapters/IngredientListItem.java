package com.djdenpa.baker.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.Ingredient;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by denpa on 11/5/2017.
 *
 * ingredient list item to show in the ingredient list and display
 */

public class IngredientListItem extends AbstractItem<IngredientListItem, IngredientListItem.ViewHolder> {
  private final Ingredient mIngredient;
  public boolean mChecked = false;
  private final String mIngredientTextTemplate;
  private String mIngredientMeasureTemplate;

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

        //this tablespoon spelling comes from the API
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

    String ingredientText = getDisplayText();
    viewHolder.mCBIngredient.setText(ingredientText);
    viewHolder.mCBIngredient.setChecked(mChecked);
  }

  public String getDisplayText(){
    String measureText = String.format(mIngredientMeasureTemplate, mIngredient.readableQuantity(), mIngredient.measure.toLowerCase());
    return String.format(mIngredientTextTemplate, measureText, mIngredient.ingredient);
  }


  static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.cb_ingredient) CheckBox mCBIngredient;

    private ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }

  public static class CheckBoxClickEvent extends ClickEventHook<IngredientListItem> {
    @Override
    public void onClick(View v, int position, FastAdapter<IngredientListItem> fastAdapter, IngredientListItem item) {
      CheckBox cb = v.findViewById(R.id.cb_ingredient);
      item.mChecked = cb.isChecked();
    }

    @Override
    public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
      if (viewHolder instanceof IngredientListItem.ViewHolder) {
        return ((IngredientListItem.ViewHolder) viewHolder).mCBIngredient;
      }
      return null;
    }
  }

}
