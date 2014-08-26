package hu.flexisys.kbr.view.tenyeszet;

import java.util.Comparator;

/**
 * Created by peter on 26/08/14.
 */
public class TenyeszetListModelComparatorByLetda implements Comparator<TenyeszetListModel> {
    @Override
    public int compare(TenyeszetListModel lhs, TenyeszetListModel rhs) {
        if (lhs.getLEDAT().getTime() < rhs.getLEDAT().getTime()) {
            return 1;
        }
        if (lhs.getLEDAT().getTime() == rhs.getLEDAT().getTime()) {
            return 0;
        }
        return -1;
    }
}
