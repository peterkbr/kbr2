package hu.flexisys.kbr.view.component.diagram;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.bongeszo.diagram.DiagramModel;
import hu.flexisys.kbr.view.bongeszo.diagram.DiagramModelLinear;

/**
 * Created by peter on 23/08/14.
 */
public class DiagramLinear extends LinearLayout {

    TextView name;
    private LinearLayout parent;
    private LinearLayout red;
    private LinearLayout yellow;
    private LinearLayout green;

    public DiagramLinear(Context context) {
        super(context);
        createView();
    }

    public DiagramLinear(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView();
    }

    private void createView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View diagramView = inflater.inflate(R.layout.component_diagram_linear, this, false);

        name = (TextView) diagramView.findViewById(R.id.diagram_name);
        parent = (LinearLayout) diagramView.findViewById(R.id.diagram_parent);
        red = (LinearLayout) diagramView.findViewById(R.id.diagram_red);
        yellow = (LinearLayout) diagramView.findViewById(R.id.diagram_yellow);
        green = (LinearLayout) diagramView.findViewById(R.id.diagram_green);
        addView(diagramView);
    }

    public void updateValues(DiagramModelLinear model) {
        name.setText(model.name);

        int weightSum = model.red + model.green + model.yellow;
        parent.setWeightSum(weightSum);

        LayoutParams params;

        params = (LayoutParams) red.getLayoutParams();
        params.weight = model.red;
        red.setLayoutParams(params);

        params = (LayoutParams) yellow.getLayoutParams();
        params.weight = model.yellow;
        yellow.setLayoutParams(params);

        params = (LayoutParams) green.getLayoutParams();
        params.weight = model.green;
        green.setLayoutParams(params);
    }
}
