package uk.ac.york.dlib.aqua;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import uk.bl.dpt.parsers.ArchiveParser;

/**
 * Parse METS to retrieve JP2000/ALTO info
 * @author  <a href="mailto:frank.feng@york.ac.uk">Frank Feng</a>
 *          
 * @version 0.1.0
 *
 */
public class MetsParser {
	Set<String> imageList = new HashSet<String>();
    Set<String> altoList  = new HashSet<String>();
    
    Set<String> imgMetsOnly   = new HashSet<String>();
    Set<String> imgZipOnly    = new HashSet<String>();
    Set<String> altoMetsOnly  = new HashSet<String>();
    Set<String> altoZipOnly   = new HashSet<String>();
    
    // format of misMatchPage is: number of pages in PDF,number of image files, number of ALTO files
    String misMatchPage       = "";
    
    /**
     * 
     * @param folderName:  the folder contains METS file
     * @param metsName:    the METS file defining metadata
     * @throws DocumentException
     * @throws IOException
     */
    public void parseMets(String folderName, String metsName) throws DocumentException, IOException {
        SAXReader saxReader = new SAXReader();
        Document metsDoc    = saxReader.read(new InputStreamReader(new FileInputStream(folderName+metsName), "UTF-8"));
        Element rootElt     = metsDoc.getRootElement();
		rootElt.addNamespace("mets", "http://www.loc.gov/METS/");
		rootElt.addNamespace("xlink", "http://www.w3.org/1999/xlink");
		
		Element imgGrpElt    = (Element) rootElt.selectSingleNode("//mets:fileGrp[@ID='IMGGRP']");
		Node imgZipAtt  = imgGrpElt.selectSingleNode("//mets:file[@ID='ZIPJP2']/mets:FLocat/@xlink:href");
		List<Node> fileElements4Img = imgGrpElt.selectNodes("mets:file/mets:file/mets:FLocat/@xlink:href");
		Set<String> imgNameSetFromMets = new HashSet<String>();
		for(Node file: fileElements4Img){
			imgNameSetFromMets.add(formatImgMetsFilename(file.getText()));
		} 
		
		String imgZipFileName = folderName + imgZipAtt.getText() + ".dat";
		Set<String> imgFileNameSetInZip = ArchiveParser.getNames(new FileInputStream(imgZipFileName));
		
		compareImgSets(imgNameSetFromMets, imgFileNameSetInZip);
		
		
		Element altoGrpElt           = (Element) rootElt.selectSingleNode("//mets:fileGrp[@ID='ALTOGRP']");
		Node altoZipAtt              = altoGrpElt.selectSingleNode("//mets:file[@ID='ZIPALTO']/mets:FLocat/@xlink:href");
		List<Node> fileElements4Alto = altoGrpElt.selectNodes("mets:file/mets:file/mets:FLocat/@xlink:href");
		Set<String> altoNameSetFromMets = new HashSet<String>();
		for(Node file: fileElements4Alto){
			altoNameSetFromMets.add(formatAltoMetsFilename(file.getText()));
		} 
				
		String altoZipFileName = folderName + altoZipAtt.getText() + ".dat";
		Set<String> altoFileNameSetInZip = ArchiveParser.getNames(new FileInputStream(altoZipFileName));
				
		compareAltoSets(altoNameSetFromMets, altoFileNameSetInZip);
		
		Node pdfFileElt = rootElt.selectSingleNode("//mets:fileGrp[@ID='PDFGRP']/mets:file/mets:FLocat/@xlink:href");
		String pdfFile  = pdfFileElt.getText() + ".dat";
		int numOfPages  = PdfParser.getNumOfPages(folderName + pdfFile); 
		if(numOfPages!=altoFileNameSetInZip.size() || numOfPages!=imgFileNameSetInZip.size())
			this.misMatchPage = numOfPages + "," + imgFileNameSetInZip.size() + "," + altoFileNameSetInZip.size();
    }
    
    public void compareImgSets(Set<String> mets, Set<String>zip) {
    	for(String metsTxt: mets) {
    		if(!zip.contains(metsTxt))
    			imgMetsOnly.add(metsTxt);
    	}
    	
    	
    	for(String zipTxt: zip) {
    		if(!mets.contains(zipTxt))
    			imgZipOnly.add(zipTxt);
    	}
    	
    }
    
    public void compareAltoSets(Set<String> mets, Set<String>zip) {
    	for(String metsTxt: mets) {
    		if(!zip.contains(metsTxt))
    			altoMetsOnly.add(metsTxt);
    	}
    	
    	for(String zipTxt: zip) {
    		if(!mets.contains(zipTxt))
    			altoZipOnly.add(zipTxt);
    	}
    	
    }
    
    private String formatImgMetsFilename(String file) {
    	return file.replace("file://./JP2/", "JP2"+File.separator);
    }
    
    private String formatAltoMetsFilename(String file) {
    	return file.replace("file://./ALTO/", "ALTO"+File.separator);
    }
    
    /**
     * Generate a report of the check
     * @param outputFolderName: the output folder of the generated report file
     * @param metsName: the METS file name that the report for
     */
    public void outputResult(String outputFolderName, String metsName) {
    	File file = new File(outputFolderName + "report_" + metsName.replace(".dat", ".txt"));
    	PrintWriter out = null;
    	
 	    try {
 			out   = new PrintWriter(new FileWriter(file));
 			out.println("-------------------" + metsName + "------------------------"); 
 			if(imgMetsOnly.size()==0 && imgZipOnly.size()==0)
 				out.println("No problem found with image files.");
 			else {
 				if(imgMetsOnly.size()>0) {
 					out.println("Following images only found in METS, not in ZIP:");
 					for(String name: imgMetsOnly) {
 						out.println("\t" + name);
 					}
 				}
 				if(imgZipOnly.size()>0) {
 					out.println("Following images only found in ZIP, not in METS:");
 					for(String name: imgZipOnly) {
 						out.println("\t" + name);
 					}
 				}
 			}
 			if(altoMetsOnly.size()==0 && altoZipOnly.size()==0)
 				out.println("No problem found with ALTO files.");
 			else {
 				if(altoMetsOnly.size()>0) {
 					out.println("Following ALTO files only found in METS, not in ZIP:");
 					for(String name: altoMetsOnly) {
 						out.println("\t" + name);
 					}
 				}
 				if(altoZipOnly.size()>0) {
 					out.println("Following ALTO files only found in ZIP, not in METS:");
 					for(String name: altoZipOnly) {
 						out.println("\t" + name);
 					}
 				}
 			}
 			
 			if("".equals(this.misMatchPage))
 				out.println("No problem found in PDF file - all associated images/ALTOs found.");
 			else
 				out.println("Mismatch found between PDF and images/ALTOs: " + misMatchPage);

 	    } catch (IOException e) {
	 		e.printStackTrace();
 	    } finally {
 			out.close();
 	    }
    }
}
