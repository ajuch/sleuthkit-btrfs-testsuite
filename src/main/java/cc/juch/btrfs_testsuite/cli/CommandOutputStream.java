package cc.juch.btrfs_testsuite.cli;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.exec.LogOutputStream;

public class CommandOutputStream extends LogOutputStream {

    private final List<String> lines = new LinkedList<>();

    @Override
    protected void processLine(String arg0, int arg1) {
        lines.add(arg0);
    }

    public List<String> getLines() {
        return lines;
    }
}
