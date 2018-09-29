package com.randomappsinc.randomnumbergeneratorplus.audio;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;

public class AudioFocusManager {

    public interface Listener {
        void onAudioFocusGranted();

        void onAudioFocusDenied();

        void onAudioFocusLost();
    }

    private Listener listener;
    private AudioManager audioManager;

    // Oreo audio focus shenanigans
    private AudioFocusRequest audioFocusRequest;

    AudioFocusManager(Context context, Listener listener) {
        this.listener = listener;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initializeOAudioFocusParams();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void initializeOAudioFocusParams() {
        AudioAttributes ttsAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANT)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
        audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(ttsAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(audioFocusChangeListener, new Handler())
                .build();
    }

    void requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestAudioFocusPostO();
        } else {
            requestAudioFocusPreO();
        }
    }

    @SuppressWarnings("deprecation")
    private void requestAudioFocusPreO() {
        int result = audioManager.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            listener.onAudioFocusGranted();
        } else {
            listener.onAudioFocusDenied();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void requestAudioFocusPostO() {
        int res = audioManager.requestAudioFocus(audioFocusRequest);
        if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            listener.onAudioFocusGranted();
        }
    }

    private final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS
                            || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                            || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        releaseAudioFocus();
                        listener.onAudioFocusLost();
                    }
                }
            };

    void releaseAudioFocus() {
        audioManager.abandonAudioFocus(audioFocusChangeListener);
    }
}
