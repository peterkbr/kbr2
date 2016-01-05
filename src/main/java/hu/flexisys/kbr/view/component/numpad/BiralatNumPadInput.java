package hu.flexisys.kbr.view.component.numpad;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import hu.flexisys.kbr.R;

import java.util.List;

public class BiralatNumPadInput extends NumPadInput {

    private NumPadInputContainer container;
    private String keszletStart;
    private String keszletEnd;
    private List<String> keszletExtensions;
    private boolean newContent;
    private boolean oldContent;

    private String kod;
    private Integer szarmaztatottContent;
    private Integer maxDiff = 3;

    private boolean stepIn = true;

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
    protected void setUp(AttributeSet attrs) {
        super.setUp(attrs);
        oldContent = false;
    }

    public void clear() {
        newContent = false;
        oldContent = false;
        numValue = "";
        setText("");
        unSelect();
        updateColor();
    }

    public void newInput() {
        newContent = true;
    }

    public void addOldContent(String oldValue) {
        if (validateContent(oldValue)) {
            oldContent = true;
            setText(oldValue);
        }
    }

    public void removeSpecialContent() {
        oldContent = false;
        newContent = false;
        numValue = "";
        setText("");
        updateColor();
    }

    @Override
    public void onValidInput(String newValue) {
        if (oldContent || newContent) {
            oldContent = false;
            newContent = false;
            numValue = "";
            setText(numValue);
        }
        super.onValidInput(newValue);
        container.onInput();
        if (hasMaxLength()) {
            if (numValue.length() == maxLength && container != null) {
                if (szarmaztatottContent != null) {
                    if (!validateSzarmaztatottErtek()) {
                        setText(String.valueOf(szarmaztatottContent), true);
                        // TODO mi az üzenet?
                        container.onMessage("A felülírott származtatott érték intervallumon kívül esik!\nViszaállítottuk az eredeti értéket.");
                    }
                }
                container.onMaxLengthReached(kod);
            }
        }
    }

    public Boolean validateSzarmaztatottErtek() {
        if (szarmaztatottContent != null) {
            String textValue = String.valueOf(getText());
            if (textValue.length() > 0) {
                int intContent = Integer.parseInt(textValue);
                int minValue = szarmaztatottContent - maxDiff;
                int maxValue = szarmaztatottContent + maxDiff;
                if (intContent < minValue || intContent > maxValue) {
                    return false;
                }
            }
        }
        return true;
    }

    protected Boolean validateContent(String newContent) {
        try {
            int intContent = Integer.parseInt(newContent);
            int length = newContent.length();
            boolean validForStart = false;
            if (keszletStart.length() >= length) {
                validForStart = (intContent >= Integer.valueOf(keszletStart.substring(0, length)));
            }
            boolean validForEnd = false;
            if (keszletEnd.length() >= length) {
                validForEnd = (intContent <= Integer.valueOf(keszletEnd.substring(0, length)));
            }
            if (validForStart && validForEnd) {
                maxLength = keszletEnd.length();
                return true;
            } else {
                for (String ke : keszletExtensions) {
                    if (ke.length() >= length && intContent == Integer.valueOf(ke.substring(0, length))) {
                        maxLength = ke.length();
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("BiralatNumPadInput", "inputValidation", e);
        }
        return false;
    }

    @Override
    public CharSequence getText() {
        if (oldContent) {
            return "";
        }
        return super.getText();
    }

    @Override
    protected void updateColor() {
        int colorResId = R.color.white;
        if (oldContent) {
            colorResId = R.color.purple;
        } else if (newContent) {
            colorResId = R.color.light_yellow;
        }
        if (selected) {
            colorResId = R.color.red;
        } else if (active) {
            colorResId = R.color.green;
        }
        setBackgroundColor(getContext().getResources().getColor(colorResId));
    }

    public boolean shoudStepInto() {
        return stepIn;
    }

    public void setText(CharSequence text, Boolean szarmaztatott) {
        super.setText(text);
        if (szarmaztatott) {
            szarmaztatottContent = Integer.valueOf(String.valueOf(text));
        }
    }

    // GETTERS, SETTERS

    public void setKod(String kod) {
        this.kod = kod;
    }

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

    public void setKeszletExtensions(List<String> keszletExtensions) {
        this.keszletExtensions = keszletExtensions;
    }

    public void setStepIn(boolean stepIn) {
        this.stepIn = stepIn;
    }
}
