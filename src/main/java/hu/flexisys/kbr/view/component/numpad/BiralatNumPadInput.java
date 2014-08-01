package hu.flexisys.kbr.view.component.numpad;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by peter on 29/07/14.
 */
public class BiralatNumPadInput extends NumPadInput {

    private NumPadInputContainer container;
    private String keszletStart;
    private String keszletEnd;

    public BiralatNumPadInput(Context context) {
        super(context);
    }

    public BiralatNumPadInput(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BiralatNumPadInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onValidInput(String newValue) {
        super.onValidInput(newValue);
        container.onInput();
        if (hasMaxLength()) {
            if (numValue.length() == maxLength && container != null) {
                container.onMaxLengthReached();
            }
        }
    }

    protected Boolean validateContent(String newContent) {
        try {
            int intContent = Integer.parseInt(newContent);
            int length = newContent.length();
            if (intContent >= Integer.valueOf(keszletStart.substring(0, length)) && intContent <= Integer.valueOf(keszletEnd.substring(0, length))) {
                return true;
            }
        } catch (Exception e) {
        }
        container.onInvalidInput();
        return false;
    }

    // GETTERS, SETTERS

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public void setContainer(NumPadInputContainer container) {
        this.container = container;
    }

    public void setKeszletStart(String keszletStart) {
        this.keszletStart = keszletStart;
    }

    public void setKeszletEnd(String keszletEnd) {
        this.keszletEnd = keszletEnd;
    }
}
