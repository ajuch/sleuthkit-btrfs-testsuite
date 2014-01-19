package cc.juch.btrfs_testsuite.cli;

import java.io.IOException;
import java.util.List;

import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CliUtil {
    private static final Logger LOG = LoggerFactory
            .getLogger(CliUtil.class);

    public static List<String> executeCli(CommandLine cli)
            throws CliException {
        Executor exec = new DefaultExecutor();
        CommandOutputStream cos = new CommandOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler(cos);
        exec.setStreamHandler(psh);
        try {
        exec.execute(cli);
        } catch (IOException ex) {
            LOG.error("Error executing command '{}'", cli.toString());
            LOG.error("Caught exception ", ex);
            for(String l : cos.getLines()) {
                LOG.error("OUT: {}", l);
            }
            throw new CliException(ex);
        }
        List<String> lines = cos.getLines();

        try {
            cos.close();
        } catch (IOException e) {
            LOG.error("Error closing Command Output Stream", e);
        }

        psh.stop();

        return lines;
    }

    public static void runInBackground(CommandLine cli) throws IOException {
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        ExecuteWatchdog watchdog = new ExecuteWatchdog(60*1000);
        Executor executor = new DefaultExecutor();
        executor.setExitValue(1);
        executor.setWatchdog(watchdog);
        executor.execute(cli, resultHandler);
    }
}
