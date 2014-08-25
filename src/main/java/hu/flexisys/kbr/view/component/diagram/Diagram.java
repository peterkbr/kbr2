package hu.flexisys.kbr.view.component.diagram;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.bongeszo.diagram.DiagramModel;

/**
 * Created by peter on 23/08/14.
 */
public class Diagram extends LinearLayout {

    TextView name;
    LinearLayout red;
    LinearLayout yellow;
    LinearLayout green;

    public Diagram(Context context) {
        super(context);
        createView();
    }

    public Diagram(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView();
    }

    private void createView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View diagramView = inflater.inflate(R.layout.component_diagram, this, false);

        name = (TextView) diagramView.findViewById(R.id.diagram_name);
        red = (LinearLayout) diagramView.findViewById(R.id.diagram_red);
        yellow = (LinearLayout) diagramView.findViewById(R.id.diagram_yellow);
        green = (LinearLayout) diagramView.findViewById(R.id.diagram_green);
        addView(diagramView);
    }

    public void updateValues(DiagramModel model) {
        name.setText(model.name);

        LinearLayout.LayoutParams params;

        params = (LinearLayout.LayoutParams) red.getLayoutParams();
        params.weight = model.red;
        red.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) yellow.getLayoutParams();
        params.weight = model.yellow;
        yellow.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) green.getLayoutParams();
        params.weight = model.green;
        green.setLayoutParams(params);
    }
}
