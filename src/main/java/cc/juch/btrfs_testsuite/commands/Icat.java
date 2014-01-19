package cc.juch.btrfs_testsuite.commands;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Icat extends TskCommand {

    private static final String ICAT = BASEDIR + "icat";
    private static final Logger LOG = LoggerFactory.getLogger(Icat.class);

    private Icat() {
        // Nicht instanzierbar
    }

    public static class IcatResult {

        private final File file;
        private final String stderr;

        public IcatResult(File f, String s) {
            this.file = f;
            this.stderr = s;
        }

        public File getFile() {
            return file;
        }

        public String getStderr() {
            return stderr;
        }
    }

    public static IcatResult getData(File image, int inode) throws ExecuteException,
            IOException {
        File tmpdir = FileUtils.getTempDirectory();
        File tmpfile = new File(tmpdir, Integer.toString(inode));

        CommandLine cl = new CommandLine(ICAT);
        cl.addArgument("-v");
        cl.addArgument("-f");
        cl.addArgument("btrfs");
        cl.addArgument(image.getAbsolutePath());
        cl.addArgument(Integer.toString(inode));

        ByteArrayOutputStream errOutput = new ByteArrayOutputStream(4096);

        PumpStreamHandler psh = new PumpStreamHandler(new FileOutputStream(
                tmpfile), errOutput);

        DefaultExecutor exec = new DefaultExecutor();
        exec.setStreamHandler(psh);
        exec.execute(cl);

        return new IcatResult(tmpfile, errOutput.toString());
    }
}
