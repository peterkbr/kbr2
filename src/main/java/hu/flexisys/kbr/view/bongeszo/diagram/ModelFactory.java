package hu.flexisys.kbr.view.bongeszo.diagram;

/**
 * Created by peter on 27/11/14.
 */
public class ModelFactory {

    public static BaseDiagramModel getDiagramModel(String valuesString) {
        String[] values = valuesString.split(",");
        if (values.length < 7) {
            return new DiagramModelLinear(values);
        } else {
            return new DiagramModel(values);
        }
    }
}
