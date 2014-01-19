package cc.juch.btrfs_testsuite;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import cc.juch.btrfs_testsuite.common.FileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PathUtil.class);

    private static class FlsListFileVisitor extends SimpleFileVisitor<Path> {

        private final Path base;
        private final List<FileMetadata> res = new ArrayList<>();

        public FlsListFileVisitor(Path base) {
            this.base = base;
        }

        @Override
        public FileVisitResult visitFile(Path p, BasicFileAttributes attrs)
                throws IOException {
            relativizeAndAdd(p, attrs);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir,
                BasicFileAttributes attrs) throws IOException {
            if (!base.equals(dir)) {
                relativizeAndAdd(dir, attrs);
            }
            return FileVisitResult.CONTINUE;
        }

        private void relativizeAndAdd(Path p, BasicFileAttributes attrs) {
            Path path = base.relativize(p);
            res.add(FileMetadata.fromVisitor(p, path, attrs));
        }

        public List<FileMetadata> getPathList() {
            return res;
        }
    }

    public static List<FileMetadata> createListing(Path p) throws IOException {
        FlsListFileVisitor lfv = new FlsListFileVisitor(p);
        Files.walkFileTree(p, lfv);
        return lfv.getPathList();
    }

    public static boolean filenamesMatch(final List<FileMetadata> a,
            final List<FileMetadata> b) {
        final List<FileMetadata> copyOfB = new ArrayList<>(b.size());
        copyOfB.addAll(b);

        for (final FileMetadata fmA : a) {
            boolean match = false;
            FileMetadata matchingElement = null;
            for (final FileMetadata fmB : copyOfB) {
                if (fmA.getRelativePath().equals(fmB.getRelativePath())) {
                    match = true;
                    matchingElement = fmB;
                    break;
                }
            }
            if (!match) {
                LOG.info("Could not find a match for path {}.", fmA.getRelativePath());
                return false;
            } else {
                copyOfB.remove(matchingElement);
            }
        }

        if (copyOfB.isEmpty()) {
            return true;
        } else {
            LOG.info("list b contains entries:");
            for (FileMetadata fmB : copyOfB) {
                LOG.info(fmB.toString());
            }
            return false;
        }
    }

    public static class Pair<T> {

        public T a;
        public T b;

        public Pair(T a, T b) {
            this.a = a;
            this.b = b;
        }
    }

    public static List<Pair<FileMetadata>> mergePaths(List<FileMetadata> a, List<FileMetadata> b) {
        List<Pair<FileMetadata>> res = new ArrayList<>();
        for (FileMetadata aInst : a) {
            for (FileMetadata bInst : b) {
                if (aInst.equals(bInst)) {
                    res.add(new Pair(aInst, bInst));
                }
            }
        }
        return res;
    }

    public static FileMetadata findPath(List<FileMetadata> fm, Path p) {
        for (FileMetadata f : fm) {
            if (f.getRelativePath().equals(p)) {
                return f;
            }
        }
        return null;
    }
}
