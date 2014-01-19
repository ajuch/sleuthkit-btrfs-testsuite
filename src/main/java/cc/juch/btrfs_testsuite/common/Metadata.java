package cc.juch.btrfs_testsuite.common;

import cc.juch.btrfs_testsuite.common.image.Image;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* Created by andreas on 12/5/13.
*/
public class Metadata implements PresenceCheck {
    private final int position;
    private final byte[] data;
    private final Image image;

    public Metadata(int position, int size, Image image) throws IOException {
        this.position = position;
        this.image = image;

        RandomAccessFile raf = new RandomAccessFile(image.getDevice(), "r");

        data = new byte[size];
        raf.seek(position);

        int read = raf.read(data);

        raf.close();

        if (read != size) {
            throw new IllegalStateException("didn't read everything!?");
        }
    }

    public int getPosition() {
        return position;
    }

    public byte[] getData() {
        return data.clone();
    }

    public boolean isPresent() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(image.getDevice(), "r");

        raf.seek(position);
        byte[] buf = new byte[data.length];
        raf.read(buf);

        raf.close();

        return Arrays.equals(data, buf);
    }

    public static List<Metadata> fromIcatStderr(String stderr, Image image) throws IOException {
        List<Metadata> results = new ArrayList<>();
        for (String line : Splitter.on('\n').split(stderr)) {
            if (line.startsWith("extent data physical addr")) {
                ArrayList<String> split = Lists.newArrayList(Splitter.on(' ')
                        .trimResults().split(line));
                results.add(new Metadata(Integer.parseInt(split.get(4)), Integer.parseInt(split.get(6)), image));
            }
        }
        return results;
    }
}
