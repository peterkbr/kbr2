package hu.flexisys.kbr.view.component.biralpanel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import hu.flexisys.kbr.R;

/**
 * Created by peter on 02/09/14.
 */
public class BiralPanel extends LinearLayout {

    private Boolean editable;

    private LinearLayout layout;
    private BiralPanelColumn currentColumn;
    private int maxHeight;

    public BiralPanel(Context context) {
        super(context);
        createView();
    }

    public BiralPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView();
    }

    private void createView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) inflater.inflate(R.layout.component_biral_panel, this, false);
        addView(layout);
    }

    public void setUp(int maxHeight) {
        this.maxHeight = maxHeight;
        layout.removeAllViews();
        addColumn();
    }

    private void addColumn() {
        BiralPanelColumn column = new BiralPanelColumn(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        column.setLayoutParams(params);
        column.setUp(maxHeight);
        layout.addView(column);
        currentColumn = column;
    }

    public void addElement(BiralPanelElement element) {
        if (currentColumn.isFull()) {
            addColumn();
        }
        currentColumn.addElement(element);
    }

    public void addBreakPoint() {
        currentColumn.close();
    }

    // GETTERS, SETTERS

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }
}
