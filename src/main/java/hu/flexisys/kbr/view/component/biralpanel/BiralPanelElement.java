package hu.flexisys.kbr.view.component.biralpanel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.component.numpad.BiralatNumPadInput;

/**
 * Created by peter on 02/09/14.
 */
public class BiralPanelElement extends LinearLayout {


    private BiralatNumPadInput input;
    private LinearLayout layout;
    private TextView label;
    private int layoutResId = R.layout.component_biral_panel_element;

    public BiralPanelElement(Context context) {
        super(context);
        createView();
    }

    public BiralPanelElement(Context context, int layoutResId) {
        super(context);
        this.layoutResId = layoutResId;
        createView();
    }

    private void createView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layoutResId, this, false);
        addView(view);
        layout = (LinearLayout) view.findViewById(R.id.bir_pan_el_layout);
        label = (TextView) view.findViewById(R.id.bir_pan_el_label);
        input = (BiralatNumPadInput) view.findViewById(R.id.bir_pan_el_input);
    }

    public BiralatNumPadInput getInput() {
        return input;
    }

    public LinearLayout getLayout() {
        return layout;
    }

    public TextView getLabel() {
        return label;
    }
}
