/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cc.juch.btrfs_testsuite;

import cc.juch.btrfs_testsuite.cli.CliException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import cc.juch.btrfs_testsuite.common.Attribute;
import cc.juch.btrfs_testsuite.common.FileImageTest;
import cc.juch.btrfs_testsuite.common.image.MountException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author andreas
 */
public class FileDeleteOverwriteTest extends FileImageTest {
    private static final String testFileName = "Wikimania_2011_-_Opening_ceremony_greetings_by_Meir_Sheetrit.ogv";
    private static final Logger LOG = LoggerFactory.getLogger(FileDeleteOverwriteTest.class);
    
    @Test
    public void testDeleteOverwrite() throws IOException, CliException, MountException {

        Path toDelete = Paths.get(testFileName);
        File testFile = new File(testDataDir, testFileName);

        Attribute attr = img.getAttribute(toDelete, testFile);

        //List<Metadata> metadataList = t.extractExtentDataPositionFromIcatStderr()
        LOG.info("Determined Attribute: {}", attr);
        LOG.info("deleting file");
        img.mount();
        img.deleteFile(toDelete);
        //img.umount();
        boolean dataPresent = attr.isPresent();
        int newFilesCreated = 0;
        assertTrue(dataPresent);
        while(dataPresent) {
            LOG.info("iteration {}", newFilesCreated);
            newFilesCreated++;
            //img.mount();
            img.createFile("file" + Integer.toString(newFilesCreated), 1024);
            //img.forceSync();
            //img.umount();
            dataPresent = attr.isPresent();
        }
        LOG.info("Data overwritten at try {}", newFilesCreated);
    }
}
