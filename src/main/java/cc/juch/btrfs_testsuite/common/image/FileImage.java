package cc.juch.btrfs_testsuite.common.image;

import cc.juch.btrfs_testsuite.common.filesystem.Filesystem;
import org.apache.commons.exec.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by andreas on 12/8/13.
 */
public class FileImage extends Image {
    private static final Logger LOG = LoggerFactory.getLogger(FileImage.class);

    public FileImage(final Filesystem fs, final File imageFile, final File mountpoint, int sizeMib) throws IOException, PrepareException {
        super(fs, imageFile, mountpoint, sizeMib);
    }


    @Override
    public CommandLine getMountCl() {
        CommandLine cl = new CommandLine("sudo");
        cl.addArgument("mount");
        cl.addArgument("-o");
        cl.addArgument("loop");
        cl.addArgument(getDevice().getAbsolutePath());
        cl.addArgument(getMountpoint().getAbsolutePath());
        return cl;
    }

    @Override
    public void prepare() throws PrepareException {
        final int size = getSizeMib() * 1024 * 1024;
        try (RandomAccessFile f = new RandomAccessFile(getDevice(), "rw")) {
            f.setLength(size);
        } catch (IOException ioe) {
            throw new PrepareException("Couldn't create image file.", ioe);
        }
    }

    @Override
    public void reset() throws ResetException {
        getDevice().delete();
    }
}
