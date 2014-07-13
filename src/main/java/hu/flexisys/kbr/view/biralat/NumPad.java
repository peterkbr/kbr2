package hu.flexisys.kbr.view.biralat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import hu.flexisys.kbr.R;

/**
 * Created by Peter on 2014.07.13..
 */
public class NumPad extends LinearLayout {

    public NumPad(Context context) {
        super(context);
        createView();
    }

    public NumPad(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView();
    }

    private void createView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View hiddenInfo = inflater.inflate(R.layout.num_pad, this, false);
        addView(hiddenInfo);
    }
}
