package cc.juch.btrfs_testsuite;

import cc.juch.btrfs_testsuite.cli.CliException;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import cc.juch.btrfs_testsuite.common.*;
import cc.juch.btrfs_testsuite.common.filesystem.Btrfs;
import cc.juch.btrfs_testsuite.common.filesystem.FormatException;
import cc.juch.btrfs_testsuite.common.image.FileImage;
import cc.juch.btrfs_testsuite.common.image.Image;
import cc.juch.btrfs_testsuite.common.image.MountException;
import cc.juch.btrfs_testsuite.common.image.PrepareException;
import org.junit.Test;

public class ImageCreatorTest extends TestBase {
	
	@Test
	public void testFormatBtrfs() throws IOException, CliException, MountException, PrepareException, FormatException {
		final int sizeMb = 1024;
		File imageFile = new File(basePath, "testBtrfs.img");
		Image i = new FileImage(new Btrfs(), imageFile, mountpoint, sizeMb);
        i.format();
        assertEquals(sizeMb * 1024 * 1024, imageFile.length());
		i.mount();
		i.umount();
		imageFile.delete();
        assertFalse(imageFile.exists());
	}
}
