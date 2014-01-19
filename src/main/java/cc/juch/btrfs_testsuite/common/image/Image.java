package cc.juch.btrfs_testsuite.common.image;

import cc.juch.btrfs_testsuite.PathUtil;
import cc.juch.btrfs_testsuite.cli.CliException;
import cc.juch.btrfs_testsuite.cli.CliUtil;
import cc.juch.btrfs_testsuite.commands.Fls;
import cc.juch.btrfs_testsuite.commands.Icat;
import cc.juch.btrfs_testsuite.common.*;
import cc.juch.btrfs_testsuite.common.filesystem.Filesystem;
import cc.juch.btrfs_testsuite.common.filesystem.FormatException;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public abstract class Image {

    private static final Logger LOG = LoggerFactory
            .getLogger(Image.class);
    private static final File MTAB = new File("/etc/mtab");
    private static final String MUST_BE_MOUNTED = "Image must be mouted.";
    private static final String MUST_NOT_BE_MOUNTED = "Image MUST NOT be mounted.";
    private final File device;
    private final File mountpoint;
    private boolean mounted = false;
    private final Filesystem fs;
    private final int sizeMib;

    Image(Filesystem fs, final File device, final File mountpoint, int sizeMib) throws PrepareException {
        this.device = device;
        this.mountpoint = mountpoint;
        this.fs = fs;
        this.sizeMib = sizeMib;
        prepare();
    }

    public abstract CommandLine getMountCl();

    abstract void prepare() throws PrepareException;

    public abstract void reset() throws ResetException;

    public void forceSync() {
        CommandLine cl = new CommandLine("sudo");
        cl.addArgument("btrfs");
        cl.addArgument("filesystem");
        cl.addArgument("sync");
        cl.addArgument(mountpoint.getAbsolutePath());
        try {
            CliUtil.executeCli(cl);
        } catch (CliException e) {
            LOG.error("Couldn't sync", e);
        }
    }

    public void mount() throws MountException {
        if (isMountpointUsed()) {
            throw new MountException("Something already mounted at mountpoint " + getMountpoint().getAbsolutePath().toString() + ".");
        }
        if (isMounted()) {
            throw new MountException("Already mounted.");
        }

        try {
            CliUtil.executeCli(getMountCl());
        } catch (CliException e) {
            throw new MountException("Couldn't mount image", e);
        }
        setMounted(true);
        try {
            changePermissions();
        } catch (CliException e) {
            throw new MountException("Couldn't change mountpoint permissions", e);
        }
    }

    public void umount() throws MountException {
        if (!mounted) {
            throw new MountException("Cannot unmount because image is not mounted.");
        } else {
            LOG.info("Trying to unmount Btrfs image at {}", mountpoint);
            CommandLine cl = new CommandLine("sudo");
            cl.addArgument("umount");
            cl.addArgument(mountpoint.getAbsolutePath());
            try {
                CliUtil.executeCli(cl);
            } catch (CliException e) {
                throw new MountException("Error umounting.", e);
            }
            mounted = false;
        }
    }

    private boolean isImageMounted() {
        return mtabLineContainsString(0, device.getAbsolutePath());
    }

    boolean isMountpointUsed() {
        return mtabLineContainsString(1, mountpoint.getAbsolutePath());
    }

    private static boolean mtabLineContainsString(int col, String s) {
        List<String> content = Collections.emptyList();
        try {
            content = Files.readLines(MTAB, Charset.defaultCharset());
        } catch (IOException ex) {
            LOG.error("Error reading mtab!", ex);
        }
        for (String line : content) {
            List<String> split = Lists.newArrayList(Splitter.on(' ')
                    .trimResults().split(line));
            if (split.get(col).equals(s)) {
                return true;
            }
        }
        LOG.info("mtab didn't contain the line.");
        return false;
    }

    public static byte[] extractPart(final File image, int startOffset, int size) throws FileNotFoundException, IOException {
        byte[] buffer;
        try (RandomAccessFile raf = new RandomAccessFile(image, "r")) {
            buffer = new byte[size];
            raf.readFully(buffer, startOffset, size);
        }
        return buffer;
    }

    public void format() throws FormatException {
        fs.format(device);
    }

    void changePermissions() throws CliException {
        if (mounted) {
            CommandLine cl = new CommandLine("sudo");
            cl.addArgument("chmod");
            cl.addArgument("-R");
            cl.addArgument("o=rwx");
            cl.addArgument(mountpoint.getAbsolutePath());
            CliUtil.executeCli(cl);
        } else {
            throw new IllegalStateException(MUST_BE_MOUNTED);
        }
    }

    public void copyTestFiles(File testDataDir) throws IOException {
        if (mounted) {
            FileUtils.copyDirectory(testDataDir, mountpoint);
        } else {
            throw new IllegalStateException(MUST_BE_MOUNTED);
        }
    }

    public void createFile(String name, int sizeKib) throws IOException {
        byte[] buf = new byte[1024];
        for(int i = 0; i < buf.length; i++) {
            buf[i] = (byte) Math.round(Math.random());
        }
        if (mounted) {
            FileOutputStream fos = new FileOutputStream(new File(mountpoint, name));
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            for (int i = 0; i < sizeKib; i++) {
                bos.write(buf);
            }
            bos.close();
            fos.close();
            LOG.info("Testfile {} created.", name);
        } else {
            throw new IllegalStateException(MUST_BE_MOUNTED);
        }
    }

    public void deleteFile(Path toDelete) throws CliException {
        if (mounted) {
            File delFile = mountpoint.toPath().resolve(toDelete).toFile();
            if (!delFile.exists()) {
                throw new IllegalArgumentException("File to delete not found");
            }
            delFile.delete();
        } else {
            throw new IllegalStateException(MUST_BE_MOUNTED);
        }
    }

    public Attribute getAttribute(Path p, File contents) throws IOException, CliException {
        if (!mounted) {
            List<FileMetadata> files = Fls.getFiles(device);
            FileMetadata fm = PathUtil.findPath(files, p);
            if (fm == null) {
                throw new IllegalArgumentException("Couldn't find file.");
            }
            Icat.IcatResult res = Icat.getData(device, fm.getInode());
            return Attribute.fromIcatStderr(res.getStderr(), this, contents);
        } else {
            throw new IllegalStateException(MUST_NOT_BE_MOUNTED);
        }
    }

    public List<Metadata> getExtentData(Path p) throws CliException, IOException {
        if(!mounted) {
            List<FileMetadata> files = Fls.getFiles(device);
            FileMetadata fm = PathUtil.findPath(files, p);
            if (fm == null) {
                throw new IllegalArgumentException("Couldn't find file.");
            }
            Icat.IcatResult res = Icat.getData(device, fm.getInode());
            return Metadata.fromIcatStderr(res.getStderr(), this);
        } else {
            throw new IllegalStateException(MUST_NOT_BE_MOUNTED);
        }
    }

    public File getDevice() {
        return device;
    }

    public File getMountpoint() {
        return mountpoint;
    }

    public boolean isMounted() {
        return mounted;
    }

    public void setMounted(boolean mounted) {
        this.mounted = mounted;
    }

    public int getSizeMib() {
        return sizeMib;
    }
}
