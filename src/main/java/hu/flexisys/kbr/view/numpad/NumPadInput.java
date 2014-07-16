package hu.flexisys.kbr.view.numpad;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import hu.flexisys.kbr.R;

/**
 * Created by Peter on 2014.07.15..
 */
public class NumPadInput extends TextView {

    private String content;
    private Boolean selected;
    private Integer maxLength;

    public NumPadInput(Context context) {
        super(context);
        setUp(null);
    }

    public NumPadInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp(attrs);
    }

    public NumPadInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setUp(attrs);
    }

    private void setUp(AttributeSet attrs) {
        if (attrs != null) {
            maxLength = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "maxLength", -1);
        }
        content = "";
        selected = false;
        updateColor();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = !selected;
                updateColor();
            }
        });
    }

    private void updateColor() {
        if (selected) {
            setBackgroundColor(getContext().getResources().getColor(R.color.pink));
        } else {
            setBackgroundColor(getContext().getResources().getColor(R.color.white));
        }
    }

    public void onInput(Integer num) {
        if (selected) {
            selected = false;
            updateColor();
            content = String.valueOf(num);
        } else {
            content += String.valueOf(num);
        }
        if (maxLength != null && !maxLength.equals(-1) && content.length() > maxLength) {
            content = content.substring(0, maxLength);
        }
        setText(content);
    }
}
