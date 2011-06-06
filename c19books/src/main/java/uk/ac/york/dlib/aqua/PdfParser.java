package uk.ac.york.dlib.aqua;

import java.io.File;
import java.io.IOException;

import org.pdfbox.pdmodel.PDDocument;

public class PdfParser {

	public static int getNumOfPages(String pdfFile) throws IOException {
		PDDocument pdf = PDDocument.load(new File(pdfFile));
		int count = pdf.getNumberOfPages();
		pdf.close();
		return count;
	}
	
}
