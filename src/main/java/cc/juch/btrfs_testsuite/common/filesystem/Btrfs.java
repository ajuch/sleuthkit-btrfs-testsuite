package cc.juch.btrfs_testsuite.common.filesystem;

import cc.juch.btrfs_testsuite.cli.CliException;
import cc.juch.btrfs_testsuite.cli.CliUtil;
import org.apache.commons.exec.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Created by andreas on 12/8/13.
 */
public class Btrfs extends Filesystem {
    private static final Logger LOG = LoggerFactory.getLogger(Btrfs.class);

    public void format(File device) throws FormatException {
        CommandLine cl = new CommandLine("sudo");
        cl.addArgument("mkfs.btrfs");
        cl.addArgument(device.getAbsolutePath());

        try {
            List<String> result = CliUtil.executeCli(cl);
            LOG.info("Format successful.");
            for (String s : result) {
                LOG.debug(s);
            }
        } catch (CliException e) {
            throw new FormatException("Error formatting " + device.getAbsolutePath() + " with Btrfs.", e);
        }
    }
}
