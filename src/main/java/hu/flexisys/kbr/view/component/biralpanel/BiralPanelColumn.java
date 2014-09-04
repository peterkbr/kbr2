package hu.flexisys.kbr.view.component.biralpanel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import hu.flexisys.kbr.R;

/**
 * Created by peter on 02/09/14.
 */
public class BiralPanelColumn extends LinearLayout {

    private LinearLayout layout;
    private Integer maxHeight;
    private Integer counter;

    public BiralPanelColumn(Context context) {
        super(context);
        createView();
    }

    public BiralPanelColumn(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView();
    }

    private void createView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_biral_panel_column, this, false);
        layout = (LinearLayout) view.findViewById(R.id.bir_pan_col_layout);
        addView(view);
    }

    public void setUp(Integer maxHeight) {
        layout.removeAllViews();
        layout.setWeightSum(maxHeight);
        this.maxHeight = maxHeight;
        counter = 0;
    }

    public void addElement(BiralPanelElement element) {
        if (counter < maxHeight) {
            layout.addView(element);
            counter++;
        }
    }

    public void close() {
        counter = maxHeight;
    }

    public boolean isFull() {
        return counter >= maxHeight;
    }

}
