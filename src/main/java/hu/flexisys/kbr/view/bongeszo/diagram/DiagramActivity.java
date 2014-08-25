package hu.flexisys.kbr.view.bongeszo.diagram;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.component.diagram.Diagram;

import java.util.ArrayList;

/**
 * Created by peter on 21/08/14.
 */
public class DiagramActivity extends KbrActivity {

    public static String VALUES_KEY = "VALUES_KEY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar.hide();
        setContentView(R.layout.activity_diagram);

        Bundle extras = getIntent().getExtras();
        ArrayList<String> values = extras.getStringArrayList(VALUES_KEY);
        ArrayList<DiagramModel> diagramModelList = new ArrayList<DiagramModel>();
        for (String valueString : values) {
            DiagramModel model = new DiagramModel(valueString);
            diagramModelList.add(model);
        }

//        ListView diagramListView = (ListView) findViewById(R.id.bongeszo_diagram_list);
//        diagramListView.setEmptyView(findViewById(R.id.empty_list_item));
//        DiagramListAdapter adapter = new DiagramListAdapter(this, 0, diagramModelList);
//        diagramListView.setAdapter(adapter);

        LinearLayout listLayout = (LinearLayout) findViewById(R.id.bongeszo_diagram_list_layout);
        for (DiagramModel model : diagramModelList) {
            Diagram diagram = new Diagram(this);
            diagram.updateValues(model);
            listLayout.addView(diagram);
        }
    }

    public void okClick(View view) {
        finish();
    }
}