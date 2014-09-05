package hu.flexisys.kbr.util;

import android.content.Context;
import android.media.MediaPlayer;
import hu.flexisys.kbr.R;

/**
 * Created by peter on 05/09/14.
 */
public class SoundUtil {

    private static Context context;
    private static MediaPlayer player;

    public static void initSoundUtil(Context context) {
        SoundUtil.context = context;
    }

    public static void errorBeep() {
        Thread t = new Thread() {
            public void run() {
                player = MediaPlayer.create(context, R.raw.error_beep);
                player.setVolume(0.05f, 0.05f);
                player.start();
            }
        };
        t.start();
    }

    public static void buttonBeep() {
        Thread t = new Thread() {
            public void run() {
                player = MediaPlayer.create(context, R.raw.button_beep);
                player.setVolume(0.05f, 0.05f);
                player.start();
            }
        };
        t.start();
    }
}
