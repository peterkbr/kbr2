package hu.flexisys.kbr.view.bongeszo.diagram;

import java.io.Serializable;

/**
 * Created by peter on 21/08/14.
 */
public class DiagramModel extends BaseDiagramModel implements Serializable {
    private static final long serialVersionUID = 0L;
    public int red;
    public int yellow;
    public int green;
    public int blue;
    public int brown;
    public int orange;

    public DiagramModel(String[] valuesArray) {
        name = valuesArray[0];
        red = Integer.parseInt(valuesArray[1]);
        yellow = Integer.parseInt(valuesArray[2]);
        green = Integer.parseInt(valuesArray[3]);
        blue = Integer.parseInt(valuesArray[4]);
        brown = Integer.parseInt(valuesArray[5]);
        orange = Integer.parseInt(valuesArray[6]);
    }
}
