/* ====================================================================
   Copyright 2011 Andrew Jackson

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package org.opf_labs.aqua;

import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;
import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.hpsf.MarkUnsupportedException;
import org.apache.poi.hpsf.NoPropertySetStreamException;
import org.apache.poi.hpsf.Property;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.Section;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.Ffn;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.hwpf.usermodel.Picture;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.io.FileOutputStream;
import java.util.Iterator;


/**
 *
 * Based on POIFSDump, which dumps the internal structure of a OLE2 file into file system
 *
 * @author Andrew Jackson
 */
public class OfficeAnalyser {
    
    public static void main(String[] args) throws Exception {
        //import org.apache.poi.poifs.dev.POIFSDump;
        //POIFSDump.main(args);
    	
    	SMOutputDocument xmldoc = SMOutputFactory.createOutputDocument(
    			SMOutputFactory.getGlobalXMLOutputFactory().createXMLStreamWriter(
    					System.out, "UTF-8"), "1.1", "UTF-8", true);
    	
    	xmldoc.setIndentation("\n ", 1, 2); // for unix linefeed, 2 spaces per level
    	
    	SMOutputElement xmlroot = xmldoc.addElement("properties");    
    	
    	// Loop through arguments:
        for (int i = 0; i < args.length; i++) {
        	SMOutputElement xd = xmlroot.addElement("document");
        	xd.addAttribute("href", args[i]);
            HWPFDocument doc = new HWPFDocument (new FileInputStream(args[i]));
            
            // SummaryInformation
            SMOutputElement sie = xd.addElement("SummaryInformation");
        	sie.addElement("ApplicationName").addCharacters(doc.getSummaryInformation().getApplicationName());
        	sie.addElement("OSVersion").addCharacters(""+doc.getSummaryInformation().getOSVersion());
         	sie.addElement("Author").addCharacters(""+doc.getSummaryInformation().getAuthor());
         	sie.addElement("CharCount").addCharacters(""+doc.getSummaryInformation().getCharCount());
         	sie.addElement("Comments").addCharacters(""+doc.getSummaryInformation().getComments());
         	sie.addElement("EditTime").addCharacters(""+doc.getSummaryInformation().getEditTime());
         	sie.addElement("Format").addCharacters(""+doc.getSummaryInformation().getFormat());
         	sie.addElement("Keywords").addCharacters(""+doc.getSummaryInformation().getKeywords());
         	sie.addElement("LastAuthor").addCharacters(""+doc.getSummaryInformation().getLastAuthor());
         	sie.addElement("PageCount").addCharacters(""+doc.getSummaryInformation().getPageCount());
         	sie.addElement("RevNumber").addCharacters(""+doc.getSummaryInformation().getRevNumber());
         	sie.addElement("SectionCount").addCharacters(""+doc.getSummaryInformation().getSectionCount());
         	sie.addElement("Security").addCharacters(""+doc.getSummaryInformation().getSecurity());
         	sie.addElement("Subject").addCharacters(""+doc.getSummaryInformation().getSubject());
         	sie.addElement("Template").addCharacters(""+doc.getSummaryInformation().getTemplate());
         	sie.addElement("Title").addCharacters(""+doc.getSummaryInformation().getTitle());
         	sie.addElement("WordCount").addCharacters(""+doc.getSummaryInformation().getWordCount());
         	sie.addElement("CreatedDateTime").addCharacters(""+doc.getSummaryInformation().getCreateDateTime());
         	sie.addElement("LastPrinted").addCharacters(""+doc.getSummaryInformation().getLastPrinted());
         	sie.addElement("LastSaveDateTime").addCharacters(""+doc.getSummaryInformation().getLastSaveDateTime());
         	sie.addElement("Thumbnail").addCharacters(""+doc.getSummaryInformation().getThumbnail());

         	// TextTable
            SMOutputElement tte = xd.addElement("TextTable");
            for( TextPiece tp : doc.getTextTable().getTextPieces() ) {
            	SMOutputElement tpe = tte.addElement("TextPiece");
            	tpe.addAttribute("isUnicode", ""+tp.getPieceDescriptor().isUnicode());
            	tpe.addCharacters(tp.getStringBuilder().toString() );
            }
            
            // DocumentSummaryInformation
            SMOutputElement dsie = xd.addElement("DocumentSummaryInformation");
        	dsie.addElement("ParCount").addCharacters(""+doc.getDocumentSummaryInformation().getParCount());
        	dsie.addElement("ByteCount").addCharacters(""+doc.getDocumentSummaryInformation().getByteCount());
        	dsie.addElement("HiddenCount").addCharacters(""+doc.getDocumentSummaryInformation().getHiddenCount());
        	dsie.addElement("LineCount").addCharacters(""+doc.getDocumentSummaryInformation().getLineCount());
        	dsie.addElement("MMClipCount").addCharacters(""+doc.getDocumentSummaryInformation().getMMClipCount());
        	dsie.addElement("NoteCount").addCharacters(""+doc.getDocumentSummaryInformation().getNoteCount());
        	dsie.addElement("SectionCount").addCharacters(""+doc.getDocumentSummaryInformation().getSectionCount());
        	dsie.addElement("SlideCount").addCharacters(""+doc.getDocumentSummaryInformation().getSlideCount());
        	dsie.addElement("Format").addCharacters(""+doc.getDocumentSummaryInformation().getFormat());
        	dsie.addElement("PresentationFormat").addCharacters(""+doc.getDocumentSummaryInformation().getPresentationFormat());
        	dsie.addElement("Company").addCharacters(""+doc.getDocumentSummaryInformation().getCompany());
        	dsie.addElement("Category").addCharacters(""+doc.getDocumentSummaryInformation().getCategory());            
        	// Sections
            for( Object os : doc.getDocumentSummaryInformation().getSections() ) {
                Section s = (Section) os;
                SMOutputElement se = dsie.addElement("Section");
                se.addElement("FormatID").addCharacters(""+s.getFormatID());
                se.addElement("CodePage").addCharacters(""+s.getCodepage());
                se.addElement("PropertyCount").addCharacters(""+s.getPropertyCount());
                for( Property sp : s.getProperties() ) {
                	SMOutputElement pe = se.addElement("Property");
                	pe.addAttribute("class", sp.getValue().getClass().getCanonicalName());
                	pe.addCharacters(sp.getValue().toString());
                }
            }
            SMOutputElement fte = xd.addElement("FontTable");
            for( Ffn f : doc.getFontTable().getFontNames() ) {
            	SMOutputElement fe = fte.addElement("Font");
            	fe.addElement("MainFontName").addCharacters(f.getMainFontName());
            	try {
            		fe.addElement("AltFontName").addCharacters(f.getAltFontName());
            	} catch( Exception e ) {
            		// Seems to fail, and no safe test found as yet.
            	}
            	fe.addElement("Size").addCharacters(""+f.getSize());
            	fe.addElement("Weight").addCharacters(""+f.getWeight());
            }
            SMOutputElement pte = xd.addElement("PicturesTable");
            for( Picture p  : doc.getPicturesTable().getAllPictures() ) {
            	SMOutputElement pe = pte.addElement("Picture");
            	pe.addElement("MimeType").addCharacters(p.getMimeType());
            	pe.addElement("Width").addCharacters(""+p.getWidth());
            	pe.addElement("Height").addCharacters(""+p.getHeight());
            	pe.addElement("HorizontalScalingFactor").addCharacters(""+p.getHorizontalScalingFactor());
            	pe.addElement("VerticalScalingFactor").addCharacters(""+p.getVerticalScalingFactor());
            	pe.addElement("Content").addCharacters(""+p.getContent());
            }
            //parseCompObj( new File(args[i]) );

            // This
            //System.out.println("Dumping " + args[i]);
            FileInputStream is = new FileInputStream(args[i]);
            POIFSFileSystem fs = new POIFSFileSystem(is);
            is.close();

            DirectoryEntry root = fs.getRoot();
            
            //dump(root);
            
        	xmldoc.closeRoot(); // important, flushes, closes output
        	
        }
   }


	public static void parseCompObj(File file) {
        Collector collector = new Collector();
        POIFSReader poifsReader = new POIFSReader();
        poifsReader.registerListener(collector, "\001CompObj");
        try {
            poifsReader.read(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // collector.classId now contains the result.

    }
    
    // http://mail-archives.apache.org/mod_mbox/poi-user/200504.mbox/%3C0IFL00BM77MPGW@mta6.srv.hcvlny.cv.net%3E
    // For CLSIDs:
    // http://anoochit.fedorapeople.org/rpmbuild/BUILD/msttcorefonts/cab-contents/wviewer.stf
    // http://www.msfn.org/board/topic/139093-create-standalone-word-97/
    // For CompObj format, not clear:
    // FlashPix Format Spec!
    
    public static class Collector implements POIFSReaderListener {
        private ClassID classId;

        public void processPOIFSReaderEvent(POIFSReaderEvent event) {
            InputStream stream = event.getStream();

            try {
                if (stream.skip(12) == 12) { // magic number for the offset to the clsid.
                    byte[] classIdBytes = new byte[ClassID.LENGTH];
                    if (stream.read(classIdBytes) == ClassID.LENGTH) {
                        classId = new ClassID(classIdBytes, 0);
                    }
                }
            } catch (IOException e) {
                // Handle error.
            }
            System.out.println("Found ClassID: "+classId);
        }
    }        
    

    public static void dump(DirectoryEntry root) throws IOException {
        System.out.println(root.getName()+" : storage CLSID "+root.getStorageClsid());
        for(Iterator it = root.getEntries(); it.hasNext();){
            Entry entry = (Entry)it.next();
            if(entry instanceof DocumentNode){
                DocumentNode node = (DocumentNode)entry;
                System.out.println("Node name: "+node.getName());
                System.out.println("Node desc: "+node.getShortDescription());
                System.out.println("Node size: "+node.getSize());
                DocumentInputStream is = new DocumentInputStream(node);
                
                try {
                    PropertySet ps = new PropertySet(is);
                    if( ps.getSectionCount() != 0 ) {
                        for( Property p : ps.getProperties() ) {
                            System.out.println("Prop: "+p.getID()+" "+p.getValue());
                        }
                    }
                } catch (NoPropertySetStreamException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                } catch (MarkUnsupportedException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                }
                //byte[] bytes = new byte[node.getSize()];
                //is.read(bytes);
                //is.close();

                //FileOutputStream out = new FileOutputStream(new File(parent, node.getName().trim()));
                //out.write(bytes);
                //out.close();
                //System.out.println("Node: "+new String(bytes).substring(0, 10));
            } else if (entry instanceof DirectoryEntry){
                DirectoryEntry dir = (DirectoryEntry)entry;
                dump(dir);
            } else {
                System.err.println("Skipping unsupported POIFS entry: " + entry);
            }
        }
    }
}
