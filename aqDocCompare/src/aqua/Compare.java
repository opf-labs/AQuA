package aqua;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.xml.transform.TransformerConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.SAXException;

import uk.ac.ox.bodleian.beam.drr.wclouds.Cloud;

import cue.lang.Counter;
import cue.lang.NGramIterator;
import cue.lang.stop.StopWords;

@Controller
public class Compare implements ServletContextAware {
	public static final int FC = 5;
	private ServletContext sc;
	
	@RequestMapping(value="/compare", method=RequestMethod.POST)
	public ModelAndView startCompare(CompareForm cf) {
		HashMap<String, String> mparts = new HashMap<String, String>();	
		StringBuilder errors = new StringBuilder();
		
		String id = UUID.randomUUID().toString();
		
		File oxf = new File(sc.getRealPath("/")+"/resources/dat/orig/"+id+".html");
		File pxf = new File(sc.getRealPath("/")+"/resources/dat/migr/"+id+".html");

		try {
			//OfficeDocer office = new OfficeDocer(cf.getOriginal().getInputStream());
			AutoDetecter office = new AutoDetecter(cf.getOriginal().getInputStream());
			office.parse();

			Counter<String> oCounter = gramCounter(office.getText(), 1);
			
			AutoDetecter pdf = new AutoDetecter(cf.getMigration().getInputStream());
			//PDFer pdf = new PDFer(cf.getMigration().getInputStream());
			pdf.parse();
			
			FileUtils.writeStringToFile(oxf, fixTitleTag(office.getXhtml()));
			mparts.put("ourl", sc.getContextPath()+"/resources/dat/orig/"+id+".html");

			FileUtils.writeStringToFile(pxf, fixTitleTag(pdf.getXhtml()));
			mparts.put("purl", sc.getContextPath()+"/resources/dat/migr/"+id+".html");
			
			Counter<String> pCounter = gramCounter(pdf.getText(), 1);
			
			String oNorm = normStr(office.getText());
			String pNorm = normStr(pdf.getText());
			
			if (oNorm.equals(pNorm)) {
				mparts.put("normresult", "<p class=\"result good\">Normalized strings match");
			} else {
				mparts.put("normresult", "<p class=\"result bad\">Normalized strings do not match");
			}
			
			String wcr = "good";
			if (oCounter.getTotalItemCount() != pCounter.getTotalItemCount()) {
				wcr = "bad";
			}
			
			mparts.put("wordcountresult", "<p class=\"result "+wcr+"\">Original: <i>"+oCounter.getTotalItemCount()
					+"&nbsp;words</i>&nbsp;&nbsp;&nbsp;Migration:&nbsp;<i>"
					+pCounter.getTotalItemCount()+"&nbsp;words</i></p>");
			
			if (mostFrequentMatch(oCounter, pCounter)) {
				mparts.put("freqresult", "<p class=\"result good\">Top "+FC+" occuring words match</p>");
						//"<p>Original ["+getMostFrequent(oCounter)+"]</p><p>Migration ["+getMostFrequent(pCounter)+"]</p>");
			} else {
				mparts.put("freqresult", "<p class=\"result bad\">Top "+FC+" occuring words do not match</p>");
			}
			
			mparts.put("metacompare", getMetdataCompareTable(office.getMetadata(), pdf.getMetadata()));
			
			mparts.put("fileOne", normStr(office.getText()));
			mparts.put("fileTwo", normStr(pdf.getText()));
			
			mparts.put("ocloud", getCloud(office.getText()));
			mparts.put("pcloud", getCloud(pdf.getText()));
			
			// These don't really show anything helpful! :-)
			// mparts.put("difftext", "<pre>"+StringEscapeUtils.escapeXml(StringUtils.difference(office.getText(), pdf.getText()))+"</pre>\n");
			// mparts.put("diffxhtml", "<pre>"+StringEscapeUtils.escapeXml(StringUtils.difference(office.getXhtml(), pdf.getXhtml()))+"</pre>\n");
			
		} catch (IOException e) {
			errors.append("<p>"+e.getMessage()+"</p>");
		} catch (TransformerConfigurationException e) {
			errors.append("<p>"+e.getMessage()+"</p>");
		} catch (SAXException e) {
			errors.append("<p>"+e.getMessage()+"</p>");
		} catch (TikaException e) {
			errors.append("<p>"+e.getMessage()+"</p>");
		}
		
		mparts.put("errors", errors.toString());
		
		return new ModelAndView("returnurl", mparts);
	}
	
	private String fixTitleTag(String s) {
		// Firefox on OpenSUSE doesn't render anything is it encounters <title/> - could be a mimetype issue or a bug!
		s = s.replaceAll("<title/>", "<!-- nothing -->");
		return s;
	}

	private String getMetdataCompareTable(Metadata m1, Metadata m2) {
		return "<table class=\"mcomp\"><tr><td>"+getMetaText(m1)+"</td><td>"+getMetaText(m2)+"</td></tr></table>\n";
	}

	private boolean mostFrequentMatch(Counter<String> oCounter,
			Counter<String> pCounter) {
		
		boolean match = true;
		
		List<String> olist = oCounter.getMostFrequent(FC);
		List<String> plist = pCounter.getMostFrequent(FC);
		
		for (String s : olist) {
			if (!plist.contains(s)) {
				match = false;
			}
		}
		
		return match;
	}
	
	private String getMostFrequent(Counter<String> counter) {
		StringBuilder sb = new StringBuilder();
		for (String s : counter.getMostFrequent(FC)) {
			sb.append(s);
			sb.append(", ");
		}
		return sb.substring(0, (sb.toString().length()-2)); // ie. without the , and the space!
	}
	
	private String getMetaText(Metadata m) {
		StringBuilder sb = new StringBuilder();
		String[] names = m.names();
		Arrays.sort(names);
		
		for (String s : names) {
			sb.append("<b>"+s+"=</b>");
			sb.append("<i>"+m.get(s)+"</i>");
			sb.append("<br />");
		}
		return sb.toString();
	}

	private Counter<String> gramCounter(String str, int n) {
		Counter<String> counter = new Counter<String>();
		// Count ngrams
		for (String ngram : new NGramIterator(n, str, Locale.ENGLISH, StopWords.English)) {
			counter.note(ngram.toLowerCase(Locale.ENGLISH));
		}
		return counter;
	}
	
	private String getCloud(String str) {
		Cloud cloud = new Cloud();
		cloud.absorb(str, 1);
		return cloud.toHTMLem();
	}
	
	private String normStr(String str) {
		str = str.replaceAll("\\s", "");
		return str;
	}

	@Override
	@Autowired
	public void setServletContext(ServletContext sc) {
		this.sc = sc;
	}
}
