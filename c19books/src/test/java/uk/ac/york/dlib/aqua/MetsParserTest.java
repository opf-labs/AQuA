package uk.ac.york.dlib.aqua;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.dom4j.DocumentException;
import org.junit.Test;

public class MetsParserTest {

	@Test
	public void testParseMets() throws DocumentException, IOException {
		MetsParser metsParser = new MetsParser();
		for(String file: FileListLoader.getFileLisst()) {
			String[] f = file.split(",");
			metsParser.parseMets(f[0], f[1]);
			metsParser.outputResult("src/test/resources/", f[1]);
		}
	}

}
