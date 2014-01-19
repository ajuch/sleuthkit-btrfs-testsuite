package cc.juch.btrfs_testsuite.common;

import java.io.IOException;

/**
 * Created by andreas on 12/15/13.
 */
public interface PresenceCheck {
    boolean isPresent() throws IOException;
}
