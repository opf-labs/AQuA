package uk.bl.dpt.parsers;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class ArchiveParserTest {
	private String zipFileName = "G:/19cbooks/674837.dat";

	@Test
	public void testGetNames() throws FileNotFoundException, IOException {
		/*
		for(String file: ArchiveParser.getNames(new FileInputStream(zipFileName))) {
			System.out.println(file);
		}
		*/
	}

	@Test
	public void testGetApacheNames() {
		
	}

}
