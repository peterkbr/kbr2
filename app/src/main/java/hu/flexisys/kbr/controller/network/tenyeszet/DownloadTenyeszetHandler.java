package hu.flexisys.kbr.controller.network.tenyeszet;

import java.util.HashMap;

public interface DownloadTenyeszetHandler {

    void onDownloadFinished(HashMap<String, String> resultMap);
}
