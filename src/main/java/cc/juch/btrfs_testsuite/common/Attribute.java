package cc.juch.btrfs_testsuite.common;

import cc.juch.btrfs_testsuite.common.image.Image;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by andreas on 12/5/13.
 */
public class Attribute implements PresenceCheck {

    private final int fileSize;
    private final List<DataRun> runs;
    private final Image image;
    private static final Logger LOG = LoggerFactory.getLogger(Attribute.class);
    private final File contents;

    public Attribute(int fileSize, List<DataRun> runs, Image image, File contents) {
        this.fileSize = fileSize;
        this.runs = runs;
        this.image = image;
        this.contents = contents;
    }

    public int getFileSize() {
        return fileSize;
    }

    public List<DataRun> getRuns() {
        return Collections.unmodifiableList(runs);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("File with size ");
        ret.append(fileSize);
        ret.append(", Attributes:\n");
        for (DataRun r : runs) {
            ret.append(r.toString());
        }
        return ret.toString();
    }

    /**
     * Returns the position of the file offset in the image file.
     *
     * @param offset
     * @return
     */
    public int getPositionOfFileOffset(int offset) {
        for (DataRun r : runs) {
            if (offset >= r.getFileOffset() && offset <= (r.getFileOffset() + r.getLength())) {
                return r.getStartOffset() + (offset % r.getLength());
            }
        }
        LOG.error("couldn't translate offset {}", offset);
        return -1;
    }

    public static Attribute fromIcatStderr(String stderr, Image image, File contents) {
        int fileSize = 0;
        List<DataRun> runs = new ArrayList<>();

        for (String line : Splitter.on('\n').split(stderr)) {
            if (line.startsWith("TSK_FS_FILE size")) {
                ArrayList<String> split = Lists.newArrayList(Splitter.on(' ')
                        .trimResults().split(line));
                fileSize = Integer.parseInt(split.get(3));
            }

            if (line.startsWith("extent_phys_start_addr")) {
                ArrayList<String> split = Lists.newArrayList(Splitter.on(' ')
                        .trimResults().split(line));
                int pos = Integer.parseInt(split.get(1));
                int size = Integer.parseInt(split.get(3));
                int offset = Integer.parseInt(split.get(5));
                runs.add(new DataRun(offset, pos, size));

            }
        }
        return new Attribute(fileSize, runs, image, contents);
    }

    public boolean isPresent() throws IOException {
        final int bufSize = 1024;

        FileInputStream fis = new FileInputStream(contents);
        RandomAccessFile raf = new RandomAccessFile(image.getDevice(), "r");

        byte[] testfileBuf = new byte[bufSize];
        byte[] imageBuf = new byte[bufSize];

        for (DataRun run : getRuns()) {
            int testfileLen = 0;

            for (int i = 0; i < fileSize; i += testfileLen) {
                raf.seek(getPositionOfFileOffset(i));
                testfileLen = fis.read(testfileBuf);
                int imageLen = raf.read(imageBuf);
                if (testfileLen == imageLen) {
                    if (!Arrays.equals(testfileBuf, imageBuf)) {
                        return false;
                    } else {
                        LOG.debug("buffer matches");
                    }
                } else {
                    // end of file
                    byte[] testfileSubBuffer = Arrays.copyOfRange(testfileBuf, 0, testfileLen);
                    byte[] imageSubBuffer = Arrays.copyOfRange(imageBuf, 0, testfileLen);
                    if (!Arrays.equals(testfileSubBuffer, imageSubBuffer)) {
                        return false;
                    }
                }
            }
        }

        fis.close();
        raf.close();

        return true;
    }

    public static class DataRun {

        private final int fileOffset;
        private final int startOffset;
        private final int length;

        public DataRun(int fileOffset, int startOffset, int length) {
            this.startOffset = startOffset;
            this.length = length;
            this.fileOffset = fileOffset;
        }

        public int getStartOffset() {
            return startOffset;
        }

        public int getLength() {
            return length;
        }

        public int getFileOffset() {
            return fileOffset;
        }

        @Override
        public String toString() {
            return "Offset of file: " + fileOffset + ", offset on disk: " + startOffset + ", extent length: " + length + "\n";
        }
    }
}
