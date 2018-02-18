package com.djdenpa.baker.ui.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.AnyRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.Step;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by denpa on 11/5/2017.
 * step item for the fast adapter
 */

public class StepListItem extends AbstractItem<StepListItem, StepListItem.ViewHolder> {
  public final Step mStep;
  public final Context mContext;

  public StepListItem(Step step, Context context){
    mStep = step;
    mContext = context;
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

  private final String[] imageFileExtensions =  new String[] {"jpg", "png", "gif", "jpeg"};

  public boolean isImage(String path){
    if (path == null){
      return false;
    }
    for (String extension : imageFileExtensions)
    {
      if (path.toLowerCase().endsWith(extension))
      {
        return true;
      }
    }
    return false;
  }

  //The logic to bind your data to the view
  @Override
  public void bindView(StepListItem.ViewHolder viewHolder, List<Object> payloads) {
    //call super so the selection is already handled for you
    super.bindView(viewHolder, payloads);

    viewHolder.mTVStep.setText(mStep.shortDescription);
    viewHolder.mTVDescription.setText(mStep.description);
    Uri thumbUri;
    if (isImage(mStep.thumbnailURL) && URLUtil.isValidUrl(mStep.thumbnailURL)) {
       thumbUri = Uri.parse(mStep.thumbnailURL);
    }else{
      thumbUri = getUriToDrawable(R.drawable.image_placeholder);

      //seems to work
      //thumbUri = Uri.parse("https://i.imgur.com/G762oxF.png");
    }
    Picasso.with(mContext).load(thumbUri).into(viewHolder.mIVStepThumbnail, new com.squareup.picasso.Callback() {
      @Override
      public void onSuccess() {
      }
      @Override
      public void onError() {
      }
    });
  }

  public final Uri getUriToDrawable(@AnyRes int drawableId) {
    Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
            "://" + mContext.getResources().getResourcePackageName(drawableId)
            + '/' + mContext.getResources().getResourceTypeName(drawableId)
            + '/' + mContext.getResources().getResourceEntryName(drawableId) );
    return imageUri;
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_step) TextView mTVStep;
    @BindView(R.id.tv_long_description) TextView mTVDescription;
    @BindView(R.id.iv_step_thumbnail) ImageView mIVStepThumbnail;

    ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}
