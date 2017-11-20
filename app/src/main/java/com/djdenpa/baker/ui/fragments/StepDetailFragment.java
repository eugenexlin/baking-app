package com.djdenpa.baker.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.djdenpa.baker.R;
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

  private Step mStep;

  private Context mContext;
  private TextView mErrorMessage;
  private  FrameLayout mExoPlayerFrameLayout;

  private SimpleExoPlayer mExoPlayer;
  private SimpleExoPlayerView mPlayerView ;
  private static MediaSessionCompat mMediaSession;
  private PlaybackStateCompat.Builder mStateBuilder;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    final View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
    mContext = getContext();


    mExoPlayerFrameLayout = rootView.findViewById(R.id.fl_exo_player);
    mPlayerView =  rootView.findViewById(R.id.pv_exo_player);
    //this will try to adapt the height of the video player to aspect ratio of the used screen space.

    rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        double fragmentWidth = (float) mExoPlayerFrameLayout.getWidth();
        //set the dimensions to 16:9 regardless of screen size.
        int heightToSet = (int)Math.floor(fragmentWidth/16.0*9.0);
        mExoPlayerFrameLayout.getLayoutParams().height = heightToSet;
        mExoPlayerFrameLayout.requestLayout();

        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
      }
    });

    return rootView;
  }

  @Override
  public void onStart() {
    super.onStart();

    initializeMediaSession();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    releasePlayer();
    mMediaSession.setActive(false);
  }

  public void displayError(String errorMessage){
    mErrorMessage.setVisibility(View.VISIBLE);
    mErrorMessage.setText(errorMessage);
  }

  public void bindStep(Step step){

    //do not rebind if it we already bound it.
    if (step.equals(mStep)){
      return;
    }
    mStep = step;

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
    }else{
      mExoPlayerFrameLayout.setVisibility(View.GONE);
    }
  }


  /**
   * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
   * and media controller.
   */
  private void initializeMediaSession() {

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


  /**
   * Initialize ExoPlayer.
   * @param mediaUri The URI of the sample to play.
   */
  private void bindExoPlayer(Uri mediaUri) {
    if (mExoPlayer != null) {
      mExoPlayer.stop();
    }

    if (mExoPlayer == null) {
      // Create an instance of the ExoPlayer.
      TrackSelector trackSelector = new DefaultTrackSelector();
      LoadControl loadControl = new DefaultLoadControl();
      mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
      mPlayerView.setPlayer(mExoPlayer);

      // Set the ExoPlayer.EventListener to this activity.
      mExoPlayer.addListener(this);

      // Prepare the MediaSource.
    }

    String userAgent = Util.getUserAgent(mContext, "BakingApp");
    MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
            mContext, userAgent), new DefaultExtractorsFactory(), null, null);
    mExoPlayer.prepare(mediaSource);
    mExoPlayer.setPlayWhenReady(true);
  }
}
