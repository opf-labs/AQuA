/**
 * 
 */
package uk.bl.dpt.aqua;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a> <a
 *         href="http://sourceforge.net/users/carlwilson-bl">carlwilson-bl AT
 *         SourceForge</a> <a
 *         href="https://github.com/carlwilson-bl">carlwilson-bl AT github</a>
 * @version 0.1
 */
public class ContentSearch {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Directory dir;
		final Map<String, Integer> frequencyMap = new HashMap<String, Integer>();
		IndexReader reader;
		try {
			reader = IndexReader.open(FSDirectory.open(new File("/data/index")));
			TermEnum terms = reader.terms();
			while (terms.next()) {
				Term term = terms.term();
				if (term.field().equals("contents")) {
					frequencyMap.put(term.text(), terms.docFreq());
				}
			}
		    reader.close();
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    // sort the term map by frequency descending
	    Map<String, Integer> sortedMap = sortByValue(frequencyMap);
	    
	    for (String term : sortedMap.keySet()) {
	    	System.out.println(term + " : " + sortedMap.get(term));
	    }
	}

	static Map sortByValue(Map map) {
	     List list = new LinkedList(map.entrySet());
	     Collections.sort(list, new Comparator() {
	          public int compare(Object o1, Object o2) {
	               return 0 - ((Comparable) ((Map.Entry) (o1)).getValue())
	              .compareTo(((Map.Entry) (o2)).getValue());
	          }
	     });

	    Map result = new LinkedHashMap();
	    for (Iterator it = list.iterator(); it.hasNext();) {
	        Map.Entry entry = (Map.Entry)it.next();
	        result.put(entry.getKey(), entry.getValue());
	    }
	    return result;
	} 
}
