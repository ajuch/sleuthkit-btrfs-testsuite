package cc.juch.btrfs_testsuite;

import cc.juch.btrfs_testsuite.cli.CliException;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import cc.juch.btrfs_testsuite.common.FileImageTest;
import cc.juch.btrfs_testsuite.common.FileMetadata;
import org.apache.commons.exec.ExecuteException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.juch.btrfs_testsuite.commands.Fls;

public class FlsTest extends FileImageTest {
	private static final Logger LOG = LoggerFactory.getLogger(FlsTest.class);

	@Test
	public void compareFilenames() throws ExecuteException, IOException, CliException {
		List<FileMetadata> fls = Fls.getFiles(testImage);
		List<FileMetadata> pathes = PathUtil
				.createListing(testDataDir.toPath());
		assertTrue("Filenames must match", PathUtil.filenamesMatch(fls, pathes));
	}
}
