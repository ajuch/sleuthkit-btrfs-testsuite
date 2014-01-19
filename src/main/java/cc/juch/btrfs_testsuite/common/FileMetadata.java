package cc.juch.btrfs_testsuite.common;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.Objects;

public class FileMetadata {

    public enum FileType {

        DIR, FILE;
    }
    private final Path rel;
    private final Path abs;
    private final int inode;
    private final long size;
    private final FileType type;

    private FileMetadata(Path abs, Path rel, int inode, long size, FileType ft) {
        this.rel = rel;
        this.abs = abs;
        this.inode = inode;
        this.size = size;
        this.type = ft;
    }

    public Path getRelativePath() {
        return rel;
    }

    public Path getAbsolutePath() {
        return abs;
    }

    public int getInode() {
        return inode;
    }

    public long getSize() {
        return size;
    }

    public FileType getFileType() {
        return type;
    }

    @Override
    public String toString() {
        return "FileMetadata [p=" + rel + ", inode=" + inode + ", size=" + size
                + "]";
    }

    public static FileMetadata fromFls(String line) {
        ArrayList<String> split = Lists.newArrayList(Splitter.on('|')
                .trimResults().split(line));
        Path p = Paths.get(split.get(1).substring(1));
        int inode = Integer.parseInt(split.get(2));
        FileType ft;
        if (split.get(3).startsWith("r")) {
            ft = FileType.FILE;
        } else {
            ft = FileType.DIR;
        }
        FileMetadata fm = new FileMetadata(null, p, inode, -1, ft);
        return fm;
    }

    public static FileMetadata fromVisitor(Path abs, Path rel, BasicFileAttributes attr) {
        FileType ft;
        if (abs.toFile().isDirectory()) {
            ft = FileType.DIR;
        } else {
            ft = FileType.FILE;
        }
        return new FileMetadata(abs, rel, -1, attr.size(), ft);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.rel);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileMetadata other = (FileMetadata) obj;
        if (!Objects.equals(this.rel, other.rel)) {
            return false;
        }
        return true;
    }

}
