package hu.flexisys.kbr.view.bongeszo.diagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import hu.flexisys.kbr.R;

import java.util.List;

/**
 * Created by peter on 21/08/14.
 */
public class DiagramListAdapter extends ArrayAdapter<DiagramModel> {

    private final List<DiagramModel> diagramModelList;

    public DiagramListAdapter(Context context, int resource, List<DiagramModel> diagramModelList) {
        super(context, resource);
        this.diagramModelList = diagramModelList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View diagramView = inflater.inflate(R.layout.component_diagram, parent, false);
        TextView name = (TextView) diagramView.findViewById(R.id.diagram_name);
        LinearLayout red = (LinearLayout) diagramView.findViewById(R.id.diagram_red);
        LinearLayout yellow = (LinearLayout) diagramView.findViewById(R.id.diagram_yellow);
        LinearLayout green = (LinearLayout) diagramView.findViewById(R.id.diagram_green);

        DiagramModel model = diagramModelList.get(position);
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

        return diagramView;
    }

    @Override
    public int getCount() {
        return diagramModelList.size();
    }
}
