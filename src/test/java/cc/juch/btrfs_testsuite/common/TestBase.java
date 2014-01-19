package cc.juch.btrfs_testsuite.common;

import java.io.File;

/**
 * Created by andreas on 1/19/14.
 */
public abstract class TestBase {
    public static final File basePath = new File(TestBase.class.getResource("/").getFile());
    public static final File testDataDir = new File(TestBase.class.getResource("/testfiles").getFile());
    public static final File mountpoint = new File("/mnt/loop");
}
