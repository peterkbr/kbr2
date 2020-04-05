package hu.flexisys.kbr.view.bongeszo.diagram;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.component.diagram.Diagram;
import hu.flexisys.kbr.view.component.diagram.DiagramLinear;

import java.util.ArrayList;

/**
 * Created by peter on 21/08/14.
 */
public class DiagramActivity extends KbrActivity {

    public static String VALUES_KEY = "VALUES_KEY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagram);

        Bundle extras = getIntent().getExtras();
        ArrayList<String> values = extras.getStringArrayList(VALUES_KEY);
        ArrayList<BaseDiagramModel> diagramModelList = new ArrayList<BaseDiagramModel>();
        for (String valueString : values) {
            BaseDiagramModel model = ModelFactory.getDiagramModel(valueString);
            diagramModelList.add(model);
        }

//        ListView diagramListView = (ListView) findViewById(R.id.bongeszo_diagram_list);
//        diagramListView.setEmptyView(findViewById(R.id.empty_list_item));
//        DiagramListAdapter adapter = new DiagramListAdapter(this, 0, diagramModelList);
//        diagramListView.setAdapter(adapter);

        LinearLayout foListLayout = (LinearLayout) findViewById(R.id.bongeszo_diagram_list_fo_layout);
        LinearLayout linear1ListLayout = (LinearLayout) findViewById(R.id.bongeszo_diagram_list_linear1_layout);
        LinearLayout linear2ListLayout = (LinearLayout) findViewById(R.id.bongeszo_diagram_list_linear2_layout);
        LinearLayout linear3ListLayout = (LinearLayout) findViewById(R.id.bongeszo_diagram_list_linear3_layout);
        for (int i = 0; i < diagramModelList.size(); i++) {
            LinearLayout layout;
            if (i < 7) {
                layout = linear1ListLayout;
            } else if (i < 14) {
                layout = linear2ListLayout;
            } else if (i < 21) {
                layout = linear3ListLayout;
            } else {
                layout = foListLayout;
            }
            BaseDiagramModel model = diagramModelList.get(i);
            if (model instanceof DiagramModelLinear) {
                DiagramLinear diagram = new DiagramLinear(this);
                diagram.updateValues((DiagramModelLinear) model);
                layout.addView(diagram);
            } else if (model instanceof DiagramModel) {
                Diagram diagram = new Diagram(this);
                diagram.updateValues((DiagramModel) model);
                layout.addView(diagram);
            }
        }
    }

    public void okClick(View view) {
        finish();
    }
}