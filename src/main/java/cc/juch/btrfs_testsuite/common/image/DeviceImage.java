package cc.juch.btrfs_testsuite.common.image;

import cc.juch.btrfs_testsuite.cli.CliException;
import cc.juch.btrfs_testsuite.cli.CliUtil;
import cc.juch.btrfs_testsuite.common.filesystem.Filesystem;
import org.apache.commons.exec.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by andreas on 12/8/13.
 */
public class DeviceImage extends Image {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceImage.class);
    private static final File blktracePath = new File(DeviceImage.class.getResource("/").getFile());

    public DeviceImage(final Filesystem fs, final File device, final File mountpoint, int sizeMib) throws PrepareException {
        super(fs, device, mountpoint, sizeMib);
    }

    @Override
    public CommandLine getMountCl() {
        CommandLine cl = new CommandLine("sudo");
        cl.addArgument("mount");
        cl.addArgument(getDevice().getAbsolutePath());
        cl.addArgument(getMountpoint().getAbsolutePath());
        return cl;
    }

    @Override
    public void prepare() throws PrepareException {
        try {
            LOG.info("zeroing device...");
            zeroDevice();
            LOG.info("device zeroed.");
        } catch (CliException e) {
            throw new PrepareException("Error zeroing device.", e);
        }
    }

    private void zeroDevice() throws CliException {
        CommandLine cl = new CommandLine("sudo");
        cl.addArgument("dd");
        cl.addArgument("if=/dev/zero");
        cl.addArgument("of=" + getDevice().getAbsolutePath());
        cl.addArgument("bs=1024");
        cl.addArgument("count=" + Integer.toString(getSizeMib() * 1024));
        CliUtil.executeCli(cl);
    }

    private void resetVmCache() {

    }

    @Override
    public void reset() throws ResetException {

    }

    public void startBlktrace(String prefix) throws CliException, IOException {
        if (isMounted()) {
            CommandLine cl = new CommandLine("sudo");
            cl.addArgument("blktrace");
            cl.addArgument("-d");
            cl.addArgument(getDevice().getAbsolutePath());
            cl.addArgument("-o");
            cl.addArgument(new File(blktracePath, prefix).getAbsolutePath());
            CliUtil.runInBackground(cl);
            LOG.info("blktrace started");
        }
    }

    public void stopBlktrace() throws CliException {
        CommandLine cl = new CommandLine("sudo");
        cl.addArgument("blktrace");
        cl.addArgument("-d");
        cl.addArgument("/dev/loop0");
        cl.addArgument("-k");
        CliUtil.executeCli(cl);
    }
}
