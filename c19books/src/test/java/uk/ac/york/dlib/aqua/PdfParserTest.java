package uk.ac.york.dlib.aqua;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class PdfParserTest {

	@Test
	public void testGetNumOfPages() throws IOException {
		int num = PdfParser.getNumOfPages("G:/19cbooksNew/674840.dat");
		assertTrue(num==22);
	}

}
