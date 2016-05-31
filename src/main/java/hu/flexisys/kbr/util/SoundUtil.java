package hu.flexisys.kbr.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import hu.flexisys.kbr.R;

public class SoundUtil {

    private static Context context;
    private static SoundPool soundPool;
    private static int buttonSounID;
    private static int errorSounID;

    public static void initSoundUtil(Context context) {
        SoundUtil.context = context;
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        buttonSounID = soundPool.load(context, R.raw.button_beep, 1);
        errorSounID = soundPool.load(context, R.raw.error_beep, 1);
    }

    public static void errorBeep() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actualVolume / maxVolume;
        soundPool.play(errorSounID, volume, volume, 1, 0, 1f);
    }

    public static void buttonBeep() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actualVolume / maxVolume;
        soundPool.play(buttonSounID, volume, volume, 1, 0, 1f);
    }
}
