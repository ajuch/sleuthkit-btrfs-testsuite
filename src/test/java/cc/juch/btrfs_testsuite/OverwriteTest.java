package cc.juch.btrfs_testsuite;

import cc.juch.btrfs_testsuite.cli.CliException;
import cc.juch.btrfs_testsuite.common.*;
import cc.juch.btrfs_testsuite.common.filesystem.FormatException;
import cc.juch.btrfs_testsuite.common.image.DeviceImage;
import cc.juch.btrfs_testsuite.common.image.MountException;
import cc.juch.btrfs_testsuite.common.image.PrepareException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by andreas on 12/7/13.
 */
public class OverwriteTest extends DiskImageTest {
    private static final String testFileName = "Wikimania_2011_-_Opening_ceremony_greetings_by_Meir_Sheetrit.ogv";
    private static final Path toDelete = Paths.get(testFileName);
    private static final File testFile = new File(testDataDir, testFileName);

    /**
     * 1MiB Testfiles, no sync, no umount, no blktrace
     * @throws cc.juch.btrfs_testsuite.common.image.PrepareException
     * @throws CliException
     * @throws cc.juch.btrfs_testsuite.common.filesystem.FormatException
     * @throws cc.juch.btrfs_testsuite.common.image.MountException
     * @throws IOException
     */
    @Test
    public void test1() throws PrepareException, CliException, FormatException, MountException, IOException {
        OverwriteTest ot = new OverwriteTest();
        ot.test(1024, false, false, false);
    }

    /**
     * 1MiB testfiles, sync, no umount, no blktrace
     * @throws PrepareException
     * @throws CliException
     * @throws FormatException
     * @throws MountException
     * @throws IOException
     */
    @Test
    public void test2() throws PrepareException, CliException, FormatException, MountException, IOException {
        OverwriteTest ot = new OverwriteTest();
        ot.test(1024, true, false, false);
    }

    private void test(int fileKiloBytes, boolean sync, boolean umount, boolean blktrace) throws CliException, MountException, IOException, PrepareException, FormatException {
        if(blktrace && umount) {
            throw new IllegalArgumentException("Cannot run with umount and blktrace at the same time");
        }
        System.out.println("Starting Test with Parameters: Kilobytes: " + fileKiloBytes + ", sync: " + sync + ", umount: " + umount);
        DeviceImage im = img;
        Attribute attr = im.getAttribute(toDelete, testFile);
        List<? extends PresenceCheck> metadata = im.getExtentData(toDelete);
        im.mount();
        if(blktrace) {
            im.startBlktrace("test" + fileKiloBytes+"kb");
        }
        im.deleteFile(toDelete);
        boolean present = attr.isPresent();
        int iterations = 0;
        while (present && everythingPresent(metadata)) {
            iterations++;
            if (umount) {
                im.umount();
                im.mount();
            }
            String testFileName = "testfile" + Integer.toString(iterations);
            im.createFile(testFileName, fileKiloBytes);
            if (sync) {
                im.forceSync();
            }
            //im.deleteFile(Paths.get(testFileName));
            present = attr.isPresent();
        }
        if(blktrace) {
            im.stopBlktrace();
        }
        im.umount();
        System.out.println("Test Result: iterations: " + iterations);
        device.delete();
    }

    private boolean everythingPresent(List<? extends PresenceCheck> checks) throws IOException {
        for(PresenceCheck p : checks) {
            if(!p.isPresent()) {
                return false;
            }
        }
        return true;
    }
}
