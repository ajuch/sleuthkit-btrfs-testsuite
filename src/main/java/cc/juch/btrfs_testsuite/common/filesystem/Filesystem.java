package cc.juch.btrfs_testsuite.common.filesystem;

import java.io.File;

/**
 * Created by andreas on 12/8/13.
 */
public abstract class Filesystem {
    public abstract void format(File device) throws FormatException;
}
