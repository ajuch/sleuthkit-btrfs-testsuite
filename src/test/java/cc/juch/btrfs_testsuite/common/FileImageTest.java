/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.juch.btrfs_testsuite.common;

import cc.juch.btrfs_testsuite.cli.CliException;
import cc.juch.btrfs_testsuite.common.filesystem.Btrfs;
import cc.juch.btrfs_testsuite.common.filesystem.FormatException;
import cc.juch.btrfs_testsuite.common.image.FileImage;
import cc.juch.btrfs_testsuite.common.image.Image;
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
 * @author andreas
 */
public abstract class FileImageTest extends TestBase {

    private static final Logger LOG = LoggerFactory.getLogger(FileImageTest.class);
    protected static final File testImage = new File(basePath, "testImage.img");

    protected static Image img;

    @Before
    public void createImage() throws IOException, CliException, MountException, PrepareException, FormatException {
        img = new FileImage(new Btrfs(), testImage, mountpoint, 1024);
        img.format();
        img.mount();
        img.copyTestFiles(testDataDir);
        img.umount();
        LOG.info("Setup Image complete");
    }

    @After
    public void deleteImage() throws CliException, MountException {
        if (img.isMounted()) {
            img.umount();
        }
        testImage.delete();
    }
}
