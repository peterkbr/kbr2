package hu.flexisys.kbr.view.component.numpad;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.util.LogUtil;
import hu.flexisys.kbr.util.SoundUtil;

/**
 * Created by Peter on 2014.07.15..
 */
public class NumPadInput extends TextView {

    protected String numValue;
    protected Boolean selected;
    protected Boolean active;
    protected Integer maxLength;

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

    protected void setUp(AttributeSet attrs) {
        if (attrs != null) {
            maxLength = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "maxLength", -1);
        }
        numValue = "";
        selected = false;
        active = false;
        updateColor();
    }

    protected void updateColor() {
        int colorResId = R.color.white;
        if (selected) {
            colorResId = R.color.pink;
        } else if (active) {
            colorResId = R.color.green;
        }
        setBackgroundColor(getContext().getResources().getColor(colorResId));
    }

    public void onInput(String input) {
        String newValue = numValue;
        if (selected) {
            newValue = input;
        } else {
            newValue += input;
        }
        if (validateContent(newValue)) {
            onValidInput(newValue);
            SoundUtil.buttonBeep();
        } else {
            SoundUtil.errorBeep();
        }
    }

    public void onValidInput(String newValue) {
        if (selected) {
            selected = false;
            active = true;
            updateColor();
        }
        numValue = newValue;
        setText(numValue);
    }

    protected Boolean validateContent(String newContent) {
        try {
            Long.parseLong(newContent);
            if (hasMaxLength() && newContent.length() > maxLength) {
                throw new Exception("Input is too long");
            }
            return true;
        } catch (Exception e) {
            Log.e(LogUtil.TAG, "Invalid input", e);
        }
        return false;
    }

    protected boolean hasMaxLength() {
        return maxLength != null && !maxLength.equals(-1);
    }

    public void select() {
        selected = true;
        active = false;
        updateColor();
    }

    public void unSelect() {
        selected = false;
        active = false;
        updateColor();
    }

    public void invertSelection() {
        selected = !selected;
        active = false;
        updateColor();
    }

}
