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
import org.apache.poi.hwpf.HWPFDocument;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
        for (int i = 0; i < args.length; i++) {
            System.out.println("Dumping " + args[i]);
            FileInputStream is = new FileInputStream(args[i]);
            POIFSFileSystem fs = new POIFSFileSystem(is);
            is.close();

            DirectoryEntry root = fs.getRoot();
            
            HWPFDocument doc = new HWPFDocument (new FileInputStream(args[i]));
            System.out.println("ApplicationName: "+doc.getSummaryInformation().getApplicationName());
            System.out.println("OSVersion: "+doc.getSummaryInformation().getOSVersion());
            System.out.println("Manager: "+doc.getDocumentSummaryInformation().getManager());
            
            parseCompObj( new File(args[i]) );

            dump(root);
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
