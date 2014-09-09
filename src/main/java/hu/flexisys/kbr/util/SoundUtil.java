package hu.flexisys.kbr.util;

import android.content.Context;
import android.media.MediaPlayer;
import hu.flexisys.kbr.R;

/**
 * Created by peter on 05/09/14.
 */
public class SoundUtil {

    private static MediaPlayer errorPlayer;
    private static MediaPlayer buttonPlayer;

    public static void initSoundUtil(Context context) {
        errorPlayer = MediaPlayer.create(context, R.raw.error_beep);
        errorPlayer.setVolume(0.05f, 0.05f);

        buttonPlayer = MediaPlayer.create(context, R.raw.button_beep);
        buttonPlayer.setVolume(0.05f, 0.05f);
    }

    public static void errorBeep() {
        errorPlayer.start();
    }

    public static void buttonBeep() {
        buttonPlayer.start();
    }
}
