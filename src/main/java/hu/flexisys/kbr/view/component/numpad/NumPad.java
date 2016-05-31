package hu.flexisys.kbr.view.component.numpad;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import hu.flexisys.kbr.R;

public class NumPad extends LinearLayout {

    private NumPadInput numPadInput;

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
        View numPadView = inflater.inflate(R.layout.component_num_pad, this, false);
        int[] ids =
                new int[]{R.id.numpad_0, R.id.numpad_1, R.id.numpad_2, R.id.numpad_3, R.id.numpad_4, R.id.numpad_5, R.id.numpad_6, R.id.numpad_7, R.id.numpad_8,
                        R.id.numpad_9, R.id.numpad_0};
        for (int id : ids) {
            TextView num = (TextView) numPadView.findViewById(id);
            num.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (numPadInput != null) {
                        TextView tv = (TextView) view;
                        String text = tv.getText().toString();
                        numPadInput.onInput(text);
                    }
                }
            });
        }
        addView(numPadView);
    }

    public NumPadInput getNumPadInput() {
        return numPadInput;
    }

    public void setNumPadInput(NumPadInput numPadInput) {
        this.numPadInput = numPadInput;
    }
}
