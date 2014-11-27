package hu.flexisys.kbr.view.bongeszo.diagram;

import java.io.Serializable;

/**
 * Created by peter on 21/08/14.
 */
public class DiagramModelLinear extends BaseDiagramModel implements Serializable {
    private static final long serialVersionUID = 0L;
    public int red;
    public int yellow;
    public int green;

    public DiagramModelLinear(String[] valuesArray) {
        name = valuesArray[0];
        red = Integer.parseInt(valuesArray[1]);
        yellow = Integer.parseInt(valuesArray[2]);
        green = Integer.parseInt(valuesArray[3]);
    }
}
