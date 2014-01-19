/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.juch.btrfs_testsuite;

import cc.juch.btrfs_testsuite.common.FileImageTest;
import cc.juch.btrfs_testsuite.common.FileMetadata;
import cc.juch.btrfs_testsuite.common.FileMetadata.FileType;
import cc.juch.btrfs_testsuite.PathUtil.Pair;
import cc.juch.btrfs_testsuite.cli.CliException;
import cc.juch.btrfs_testsuite.commands.Fls;
import cc.juch.btrfs_testsuite.commands.Icat;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andreas
 */
public class IcatTest extends FileImageTest {
    private static final Logger LOG = LoggerFactory.getLogger(IcatTest.class);

    @Test
    public void icatTest() throws IOException, CliException {
        List<FileMetadata> testMetadata = PathUtil.createListing(testDataDir.toPath());
        List<FileMetadata> imageMetadata = Fls.getFiles(testImage);

        List<Pair<FileMetadata>> metadata = PathUtil.mergePaths(testMetadata, imageMetadata);

        assertTrue(metadata.size() > 0);

        for (Pair<FileMetadata> p : metadata) {
            if (p.a.getFileType() == FileType.FILE) {
                LOG.info("compare metadata of testfile {} with icat output of inode {}", p.a.getAbsolutePath().toString(), p.b.getInode());
                File f = Icat.getData(testImage, p.b.getInode()).getFile();
                assertTrue(FileUtils.contentEquals(p.a.getAbsolutePath().toFile(), f));
                f.delete();
            } else {
                LOG.info("skipping directory {}", p.a.getAbsolutePath());
            }
        }
    }
}
