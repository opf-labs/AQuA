package uk.ac.york.dlib.aqua;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class FileListLoaderTest {

	@Test
	public void testGetFileLisst() {
		List<String> files = FileListLoader.getFileLisst();
		assertNotNull(files);
	}

}
