/**
 * 
 */
package uk.bl.dpt.parsers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

/**
 * 
 * @author  <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *          <a href="http://sourceforge.net/users/carlwilson-bl">Carl Wilson @ SourceForge</a>
 *          <a href="https://github.com/carlwilson-bl">Carl Wilson @ github</a>
 * @version 0.1.0
 *
 */
public class ArchiveParser {
	/**
	 * @param is an InputStream to parse the zip from
	 * @return the set of names in this zip file
	 * @throws IOException if the stream can't be read
	 */
	static public Set<String> getNames(InputStream is) throws IOException {
		Set<String> names = new HashSet<String>();
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {
			if (!entry.isDirectory())
				names.add(entry.getName());
		}
		zis.close();
		return names;
	}
	/**
	 * @param is
	 * @return
	 * @throws IOException
	 * @throws ArchiveException
	 */
	static public Set<String> getApacheNames(InputStream is) throws IOException, ArchiveException {
		Set<String> names = new HashSet<String>();
		ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(is);
		ArchiveEntry entry;
		while ((entry = ais.getNextEntry()) != null) {
			if (!entry.isDirectory())
				names.add(entry.getName());
		}
		ais.close();
		return names;
	}
	
	/**
	 * @param is
	 * @param name
	 * @return
	 * @throws IOException
	 */
	static public InputStream getElementStream(InputStream is, String name) throws IOException {
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {
			if (entry.getName().equals(name)) {
				return zis;
			}
		}
		zis.close();
		return null;
	}
}
