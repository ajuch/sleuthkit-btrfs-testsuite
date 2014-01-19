/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.juch.btrfs_testsuite.common;

import cc.juch.btrfs_testsuite.cli.CliException;
import cc.juch.btrfs_testsuite.common.filesystem.Btrfs;
import cc.juch.btrfs_testsuite.common.filesystem.FormatException;
import cc.juch.btrfs_testsuite.common.image.DeviceImage;
import cc.juch.btrfs_testsuite.common.image.MountException;
import cc.juch.btrfs_testsuite.common.image.PrepareException;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Base class for file-based image tests.
 *
 * @author andreas
 */
public abstract class DiskImageTest extends TestBase {

    protected static final File device = new File("/dev/vgraid5/btrfstest");
    private static final Logger LOG = LoggerFactory.getLogger(DiskImageTest.class);

    protected static DeviceImage img;

    @Before
    public void prepareImage() throws IOException, CliException, MountException, PrepareException, FormatException {
        img = new DeviceImage(new Btrfs(), device, mountpoint, 1024);
        img.format();
        img.mount();
        img.copyTestFiles(testDataDir);
        img.umount();
        LOG.info("Setup Image complete");
    }

    @After
    public void umount() throws CliException, MountException {
        img.umount();
    }
}
