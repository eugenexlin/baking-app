package com.djdenpa.baker.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.djdenpa.baker.R;
import com.djdenpa.baker.core.Recipe;
import com.djdenpa.baker.core.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by denpa on 11/5/2017.
 *
 * A lot of this was copied from Classical Music Quiz
 */

public class StepDetailFragment extends Fragment implements ExoPlayer.EventListener {

  private static final String TAG = "STEP_DETAIL_TAG";
  private static final String NOW_PLAYING_STATE = "NOW_PLAYING_STATE";

  private Recipe mRecipe;
  //this is so it does not collide with index 0
  private int mStepIndex = -1;

  private Context mContext;
  private SetStepIndexHandler mSetStepIndexHandler;
  private TextView mErrorMessage;
  private TextView mStepShortDescription;
  private TextView mStepDescription;
  private ImageView mButtonPlaceholder;
  private ScrollView mScrollView;
  private Button mNextStepButton;
  private FrameLayout mExoPlayerFrameLayout;

  private View mRootView;

  private static SimpleExoPlayer mExoPlayer;
  private SimpleExoPlayerView mPlayerView ;
  private static MediaSessionCompat mMediaSession;
  private PlaybackStateCompat.Builder mStateBuilder;

  private String mNowPlaying = "";

  public interface SetStepIndexHandler{
    void handleSetStepIndex(int index);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    final View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
    mContext = getContext();

    if (mContext instanceof SetStepIndexHandler){
      mSetStepIndexHandler = (SetStepIndexHandler) mContext;
    }

    mErrorMessage = rootView.findViewById(R.id.tv_error_message);
    mStepShortDescription =  rootView.findViewById(R.id.tv_short_description);
    mStepDescription =  rootView.findViewById(R.id.tv_step_description);

    mNextStepButton = rootView.findViewById(R.id.b_next_step);
    mButtonPlaceholder = rootView.findViewById(R.id.iv_button_placeholder);

    mScrollView = rootView.findViewById(R.id.sv_step_detail);

    mNextStepButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        bindStep(mStepIndex + 1);
        mScrollView.scrollTo(0,0);
      }
    });

    mNextStepButton.setVisibility(View.GONE);

    mExoPlayerFrameLayout = rootView.findViewById(R.id.fl_exo_player);
    mPlayerView =  rootView.findViewById(R.id.pv_exo_player);

    makeSureExoPlayerInitialized();

    mPlayerView.setPlayer(mExoPlayer);

    mRootView = rootView;

    //hide it when the fragment is first made, because we want default nothing there
    // until a step is loaded
    mExoPlayerFrameLayout.setVisibility(View.GONE);


    if(savedInstanceState != null){
      if (savedInstanceState.containsKey(NOW_PLAYING_STATE)){
        mNowPlaying = savedInstanceState.getString(NOW_PLAYING_STATE);
      }
    }

    return rootView;
  }

  private void makeSureExoPlayerInitialized(){

    if (mExoPlayer == null) {
      // Create an instance of the ExoPlayer.
      TrackSelector trackSelector = new DefaultTrackSelector();
      LoadControl loadControl = new DefaultLoadControl();
      mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);

      // Set the ExoPlayer.EventListener to this activity.
      mExoPlayer.addListener(this);

      mPlayerView.setPlayer(mExoPlayer);
    }

  }

  @Override
  public void onStart() {
    super.onStart();

    initializeMediaSession();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();

    boolean isOrientationChange = false;
    if(mContext instanceof Activity && ((Activity)mContext).isChangingConfigurations()){
      isOrientationChange = true;
    }
    if (isOrientationChange) {

      mExoPlayer.setPlayWhenReady(false);
      //this DOES pause the video if you put in sleep time..!
      //but otherwise it seems to not pause because it does not pause right away, not "READY"
      //and by then the activity is back up and it is flagged to be playing again.

      //I then unfortunately get some video lag, while the audio keeps going.
      //That may or may not be what people want.
      //but this is for now not easily in my control as far as i know.

    }else{
      releasePlayer();
      mMediaSession.setActive(false);
    }

  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(NOW_PLAYING_STATE, mNowPlaying);
  }

  //
  public void hideFullScreenButton(){

  }

  public void displayError(String errorMessage){
    mErrorMessage.setVisibility(View.VISIBLE);
    mErrorMessage.setText(errorMessage);
    mExoPlayerFrameLayout.setVisibility(View.GONE);
  }

  public void unloadStep(){
    mButtonPlaceholder.setVisibility(View.GONE);
    mNextStepButton.setVisibility(View.GONE);
    mStepShortDescription.setText("");
    mStepDescription.setText("");
  }

  public void setHasNextStep(boolean hasNextStep){
    if(hasNextStep){
      mButtonPlaceholder.setVisibility(View.VISIBLE);
      mNextStepButton.setVisibility(View.VISIBLE);
    }else{
      mButtonPlaceholder.setVisibility(View.GONE);
      mNextStepButton.setVisibility(View.GONE);
    }
  }

  public void setRecipe(Recipe recipe){
    mRecipe = recipe;
  }

  public void bindStep(int index){

    if(index == mStepIndex){
      return;
    }

    unloadStep();

    if(mRecipe == null){
      return;
    }

    if(index >= mRecipe.steps().size() ){
      return;
    }

    Step step = mRecipe.steps().get(index);

    //save index for next button
    mStepIndex = index;
    setHasNextStep(index < mRecipe.steps().size()-1);

    if (mSetStepIndexHandler != null) {
     mSetStepIndexHandler.handleSetStepIndex(index);
    }

    mStepShortDescription.setText(step.shortDescription);
    mStepDescription.setText(step.description);

    boolean showPlayer = false;
    try
    {
      if (step.videoURL.length() > 0) {
        Uri videoUri = Uri.parse(step.videoURL);
        if  (videoUri != null){
          showPlayer = true;
          bindExoPlayer(videoUri);
        }
      }
    }catch(Exception ex){
      showPlayer = false;
      Log.e("EXO_PLAYER", "Failed to load video: " + ex.getLocalizedMessage());
    }

    if (showPlayer) {
      mExoPlayerFrameLayout.setVisibility(View.VISIBLE);
      delayedResizeExoPlayer();
    }else{
      if (mExoPlayer != null) {
        mExoPlayer.stop();
      }
      mExoPlayerFrameLayout.setVisibility(View.GONE);
    }
  }
//
//  @Override
//  public void onConfigurationChanged(Configuration newConfig) {
//    super.onConfigurationChanged(newConfig);
//    mRootView.requestLayout();
//    delayedResizeExoPlayer();
//  }

  public void delayedResizeExoPlayer(){
    //this will try to adapt the height of the video player to aspect ratio of the used screen space.
    mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        double fragmentWidth = (float) mExoPlayerFrameLayout.getWidth();
        //set the dimensions to 16:9 regardless of screen size.
        int heightToSet = (int)Math.floor(fragmentWidth/16.0*9.0);
        mExoPlayerFrameLayout.getLayoutParams().height = heightToSet;
        mExoPlayerFrameLayout.requestLayout();
        mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
      }
    });
  }

  /**
   * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
   * and media controller.
   */
  private void initializeMediaSession() {

    if (mMediaSession == null) {
      // Create a MediaSessionCompat.
      mMediaSession = new MediaSessionCompat(mContext, TAG);

      // Enable callbacks from MediaButtons and TransportControls.
      mMediaSession.setFlags(
              MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                      MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

      // Do not let MediaButtons restart the player when the app is not visible.
      mMediaSession.setMediaButtonReceiver(null);

      // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
      mStateBuilder = new PlaybackStateCompat.Builder()
              .setActions(
                      PlaybackStateCompat.ACTION_PLAY |
                              PlaybackStateCompat.ACTION_PAUSE |
                              PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                              PlaybackStateCompat.ACTION_PLAY_PAUSE);

      mMediaSession.setPlaybackState(mStateBuilder.build());


      // MySessionCallback has methods that handle callbacks from a media controller.
      mMediaSession.setCallback(new MySessionCallback());

      // Start the Media Session since the activity is active.
      mMediaSession.setActive(true);

    }
  }


  /**
   * Release ExoPlayer.
   */
  private void releasePlayer() {
    if (mExoPlayer != null) {
      mExoPlayer.stop();
      mExoPlayer.release();
      mExoPlayer = null;
    }
  }

  @Override
  public void onTimelineChanged(Timeline timeline, Object manifest) {

  }

  @Override
  public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

  }

  @Override
  public void onLoadingChanged(boolean isLoading) {

  }

  @Override
  public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

  }

  @Override
  public void onPlayerError(ExoPlaybackException error) {

  }

  @Override
  public void onPositionDiscontinuity() {

  }

  /**
   * Media Session Callbacks, where all external clients control the player.
   */
  private class MySessionCallback extends MediaSessionCompat.Callback {
    @Override
    public void onPlay() {
      mExoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onPause() {
      mExoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onSkipToPrevious() {
      mExoPlayer.seekTo(0);
    }
  }

  private boolean mSkipOneExoBind = false;
  public void skipOneExoBind(){
    mSkipOneExoBind = true;
  }

  /**
   * Initialize ExoPlayer.
   * @param mediaUri The URI of the sample to play.
   */
  private void bindExoPlayer(Uri mediaUri) {

    String uriString = mediaUri.toString();
    //do not restart if binding the same thing
    //especially if changing orientation.
    if(mNowPlaying.equals(uriString)){

      if (mExoPlayer != null) {
        mExoPlayer.setPlayWhenReady(true);
      }
      return;
    }

    makeSureExoPlayerInitialized();

    if (mExoPlayer != null) {
      mExoPlayer.stop();

      // Prepare the MediaSource.
      String userAgent = Util.getUserAgent(mContext, "BakingApp");
      MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
              mContext, userAgent), new DefaultExtractorsFactory(), null, null);
      mExoPlayer.prepare(mediaSource);
      mExoPlayer.setPlayWhenReady(true);

      mNowPlaying = uriString;

    }
  }
}
