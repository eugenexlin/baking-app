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
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by denpa on 11/5/2017.
 *
 * A lot of this was copied from Classical Music Quiz
 */

public class StepDetailsFragment extends Fragment implements ExoPlayer.EventListener {

  private static final String TAG = "STEP_DETAIL_TAG";
  private static final String NOW_PLAYING_STATE = "NOW_PLAYING_STATE";
  private static final String PLAYBACK_POSITION = "PLAYBACK_POSITION";

  private Recipe mRecipe;
  //this is so it does not collide with index 0
  private int mStepIndex = -1;

  private Context mContext;
  private SetStepIndexHandler mSetStepIndexHandler;
  @BindView(R.id.tv_error_message) TextView mErrorMessage;
  @BindView(R.id.tv_short_description)  TextView mStepShortDescription;
  @BindView(R.id.tv_step_description)  TextView mStepDescription;
  @BindView(R.id.iv_button_placeholder)  ImageView mButtonPlaceholder;
  @BindView(R.id.sv_step_detail)  ScrollView mScrollView;
  @BindView(R.id.b_next_step)  Button mNextStepButton;
  @BindView(R.id.fl_exo_player)  FrameLayout mExoPlayerFrameLayout;
  @BindView(R.id.pv_exo_player)  SimpleExoPlayerView mPlayerView ;

  private SimpleExoPlayer mExoPlayer;

  private static MediaSessionCompat mMediaSession;
  private PlaybackStateCompat.Builder mStateBuilder;

  private View mRootView;

  private String mNowPlaying = "";
  private long mPlaybackPosition = 0L;

  public interface SetStepIndexHandler{
    void handleSetStepIndex(int index);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    final View rootView = inflater.inflate(R.layout.fragment_step_details, container, false);
    mContext = getContext();

    ButterKnife.bind(this, rootView);

    initializeMediaSession();

    if (mContext instanceof SetStepIndexHandler){
      mSetStepIndexHandler = (SetStepIndexHandler) mContext;
    }

    mNextStepButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        bindStep(mStepIndex + 1);
        mScrollView.scrollTo(0,0);
      }
    });

    mNextStepButton.setVisibility(View.GONE);

    mRootView = rootView;

    //hide it when the fragment is first made, because we want default nothing there
    // until a step is loaded
    mExoPlayerFrameLayout.setVisibility(View.GONE);

    if(savedInstanceState != null){
      if (savedInstanceState.containsKey(NOW_PLAYING_STATE) && savedInstanceState.containsKey(PLAYBACK_POSITION)){
        mNowPlaying = savedInstanceState.getString(NOW_PLAYING_STATE);
        mPlaybackPosition = savedInstanceState.getLong(PLAYBACK_POSITION);
        restoreExoPlayer();
      }

    }

    return rootView;
  }

  private void makeSureExoPlayerInitialized(){

    if (mPlayerView.getPlayer() == null) {
      if (mExoPlayer == null) {
        // Create an instance of the ExoPlayer.
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);

        // Set the ExoPlayer.EventListener to this activity.
        mExoPlayer.addListener(this);

        mPlayerView.setPlayer(mExoPlayer);
      }
      mPlayerView.setPlayer(mExoPlayer);

    }

  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();

    //this block of code is no longer necessary after implementing always releasing and restoring play position.

//    boolean isOrientationChange = false;
//    if(mContext instanceof Activity && ((Activity)mContext).isChangingConfigurations()){
//      isOrientationChange = true;
//    }
//    if (isOrientationChange) {
//
//      //mExoPlayer.setPlayWhenReady(false);
//    }else{
//      releasePlayer();
//      mMediaSession.setActive(false);
//    }
  }

  public int getStepIndex(){
    return mStepIndex;
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(NOW_PLAYING_STATE, mNowPlaying);
    outState.putLong(PLAYBACK_POSITION, mExoPlayer.getCurrentPosition());
  }

  public void displayError(String errorMessage){
    mErrorMessage.setVisibility(View.VISIBLE);
    mErrorMessage.setText(errorMessage);
    mExoPlayerFrameLayout.setVisibility(View.GONE);
  }

  private void unloadStep(){
    mButtonPlaceholder.setVisibility(View.GONE);
    mNextStepButton.setVisibility(View.GONE);
    mStepShortDescription.setText("");
    mStepDescription.setText("");
  }

  private void setHasNextStep(boolean hasNextStep){
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

  public boolean shouldOpenFullActivity(int requestedStep){
    if (requestedStep == mStepIndex){
      if (mExoPlayer != null){
        return true;
      }
    }
    return false;
  }

  public void bindStep(int index){


    if(index >= mRecipe.steps().size() ){
      return;
    }
    Step step = mRecipe.steps().get(index);

    if(index != mStepIndex){
      mStepIndex = index;

      unloadStep();

      if(mRecipe == null){
        return;
      }

      //save index for next button
      setHasNextStep(index < mRecipe.steps().size()-1);

      if (mSetStepIndexHandler != null) {
       mSetStepIndexHandler.handleSetStepIndex(index);
      }

      mStepShortDescription.setText(step.shortDescription);
      mStepDescription.setText(step.description);

    }

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
      releasePlayer();
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

  private void delayedResizeExoPlayer(){
    //this will try to adapt the height of the video player to aspect ratio of the used screen space.
    mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        double fragmentWidth = (float) mExoPlayerFrameLayout.getWidth();
        //set the dimensions to 16:9 regardless of screen size.
        mExoPlayerFrameLayout.getLayoutParams().height = (int)Math.floor(fragmentWidth/16.0*9.0);
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

    }

  }

  @Override
  public void onPause() {
    super.onPause();
    if (mExoPlayer != null) {
      mPlaybackPosition = mExoPlayer.getCurrentPosition();
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    restoreExoPlayer();

  }

  @Override
  public void onStop() {
    super.onStop();

    if( mExoPlayer != null) {
      releasePlayer();
    }
  }

  /**
   * Release ExoPlayer.
   */
  private void releasePlayer() {
    mPlayerView.setPlayer(null);
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
    if(mNowPlaying.equals(uriString) && mExoPlayer != null){
      mExoPlayer.setPlayWhenReady(true);
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

  private void restoreExoPlayer(){
    if (mExoPlayer != null){
      return;
    }

    makeSureExoPlayerInitialized();

    Uri mediaUri = Uri.parse(mNowPlaying);

    // Prepare the MediaSource.
    String userAgent = Util.getUserAgent(mContext, "BakingApp");
    MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
            mContext, userAgent), new DefaultExtractorsFactory(), null, null);
    mExoPlayer.prepare(mediaSource);

    mExoPlayer.setPlayWhenReady(true);

    mExoPlayer.seekTo(mPlaybackPosition);

  }
}
