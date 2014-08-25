package hu.flexisys.kbr.view.bongeszo.diagram;

import java.io.Serializable;

/**
 * Created by peter on 21/08/14.
 */
public class DiagramModel implements Serializable {
    private static final long serialVersionUID = 0L;
    public String name;
    public int red;
    public int yellow;
    public int green;

    public DiagramModel(String values) {
        String[] valuesArray = values.split(",");
        name = valuesArray[0];
        red = Integer.parseInt(valuesArray[1]);
        yellow = Integer.parseInt(valuesArray[2]);
        green = Integer.parseInt(valuesArray[3]);
    }
}
