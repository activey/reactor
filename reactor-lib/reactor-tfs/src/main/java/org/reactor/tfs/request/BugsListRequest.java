package org.reactor.tfs.request;

import org.reactor.annotation.ReactorRequestParameter;

/**
 * @author grabslu
 */
public class BugsListRequest {

    @ReactorRequestParameter(shortName = "co", description = "Prints out only count of all bugs found")
    private boolean countOnly;

    public boolean isCountOnly() {
        return countOnly;
    }

    public void setCountOnly(boolean countOnly) {
        this.countOnly = countOnly;
    }
}
