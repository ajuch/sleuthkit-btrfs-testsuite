package cc.juch.btrfs_testsuite.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.juch.btrfs_testsuite.common.FileMetadata;
import cc.juch.btrfs_testsuite.cli.CliException;
import cc.juch.btrfs_testsuite.cli.CliUtil;

public final class Fls extends TskCommand {

    private static final Logger LOG = LoggerFactory.getLogger(Fls.class);
    private static String cmd = BASEDIR + "fls";

    private Fls() {
        // Nicht instanzierbar
    }

    public static List<FileMetadata> getFiles(final File image) throws
            CliException {
        CommandLine cl = new CommandLine(cmd);
        cl.addArgument("-f");
        cl.addArgument("btrfs");
        cl.addArgument("-r");
        cl.addArgument("-m");
        cl.addArgument("/");
        cl.addArgument("-a");
        cl.addArgument(image.getAbsolutePath());

        LOG.info("Calling command line {}", cl);
        List<String> ret = CliUtil.executeCli(cl);
        List<FileMetadata> result = new ArrayList<>(ret.size());

        for (String s : ret) {
            result.add(FileMetadata.fromFls(s));
        }

        return result;
    }
}
