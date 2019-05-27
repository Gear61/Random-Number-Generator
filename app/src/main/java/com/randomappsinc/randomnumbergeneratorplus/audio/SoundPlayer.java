package com.randomappsinc.randomnumbergeneratorplus.audio;

import android.content.Context;
import android.media.MediaPlayer;

import androidx.annotation.Nullable;

import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;

public class SoundPlayer {

    public interface Listener {
        void onAudioComplete();

        void onAudioError();
    }

    private final MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            audioFocusManager.releaseAudioFocus();
            currentlyPlayingMediaPlayer = null;
            listener.onAudioComplete();
        }
    };

    private final AudioFocusManager.Listener audioFocusListener = new AudioFocusManager.Listener() {
        @Override
        public void onAudioFocusGranted() {
            if (currentlyPlayingMediaPlayer != null) {
                currentlyPlayingMediaPlayer.seekTo(0);
                currentlyPlayingMediaPlayer.start();
            }
        }

        @Override
        public void onAudioFocusDenied() {
            listener.onAudioError();
        }

        @Override
        public void onAudioFocusLost() {
            if (currentlyPlayingMediaPlayer != null) {
                currentlyPlayingMediaPlayer.pause();
                currentlyPlayingMediaPlayer = null;
            }
        }
    };

    private Listener listener;
    private MediaPlayer rngSoundPlayer;
    private MediaPlayer diceSoundPlayer;
    private MediaPlayer lottoSoundPlayer;
    private MediaPlayer coinSoundPlayer;
    private @Nullable MediaPlayer currentlyPlayingMediaPlayer;
    private AudioFocusManager audioFocusManager;

    public SoundPlayer(Context context, Listener listener) {
        this.listener = listener;
        rngSoundPlayer = MediaPlayer.create(context, R.raw.rng_noise);
        rngSoundPlayer.setOnCompletionListener(onCompletionListener);
        diceSoundPlayer = MediaPlayer.create(context, R.raw.dice_roll);
        diceSoundPlayer.setOnCompletionListener(onCompletionListener);
        lottoSoundPlayer = MediaPlayer.create(context, R.raw.lotto_scratch);
        lottoSoundPlayer.setOnCompletionListener(onCompletionListener);
        coinSoundPlayer = MediaPlayer.create(context, R.raw.coin_flip);
        coinSoundPlayer.setOnCompletionListener(onCompletionListener);

        audioFocusManager = new AudioFocusManager(context, audioFocusListener);
    }

    public void playSound(@RNGType int rngType) {
        audioFocusManager.releaseAudioFocus();
        if (currentlyPlayingMediaPlayer != null) {
            currentlyPlayingMediaPlayer.pause();
            currentlyPlayingMediaPlayer = null;
        }
        switch (rngType) {
            case RNGType.NUMBER:
                currentlyPlayingMediaPlayer = rngSoundPlayer;
                break;
            case RNGType.DICE:
                currentlyPlayingMediaPlayer = diceSoundPlayer;
                break;
            case RNGType.LOTTO:
                currentlyPlayingMediaPlayer = lottoSoundPlayer;
                break;
            case RNGType.COINS:
                currentlyPlayingMediaPlayer = coinSoundPlayer;
                break;
        }
        audioFocusManager.requestAudioFocus();
    }

    public void silence() {
        if (currentlyPlayingMediaPlayer != null) {
            audioFocusManager.releaseAudioFocus();
            currentlyPlayingMediaPlayer.pause();
            currentlyPlayingMediaPlayer = null;
        }
    }
}
