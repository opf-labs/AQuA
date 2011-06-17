package aqua;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.tika.exception.TikaException;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.language.ProfilingHandler;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


public class PDFer {
	InputStream input;
	ByteArrayOutputStream baxhtml;
	ByteArrayOutputStream batext;
	Metadata meta;

	public PDFer(InputStream input) {
		this.input = input;
		baxhtml = new ByteArrayOutputStream();
		batext = new ByteArrayOutputStream();
		meta = new Metadata();
	}

	public void parse() throws TransformerConfigurationException, IOException, SAXException, TikaException {
		// set up handlers

		ProfilingHandler profiler = new ProfilingHandler();
		TransformerHandler xhtml = getXHTMLHandler();
		BodyContentHandler text = new BodyContentHandler(batext);

		ContentHandler tee = new TeeContentHandler(xhtml, text);

		PDFParser parser = new PDFParser();
		parser.parse(input, tee, meta, new ParseContext());
		
		// Nb. that because we're adding to the metadata *after* the Transformer/BodyContent handlers,
		// these changes DO NOT appear in the metadata...

		LanguageIdentifier identifier = profiler.getLanguage();

		if (identifier.isReasonablyCertain()) {
			meta.set(Metadata.LANGUAGE, identifier.getLanguage());
		} else {
			meta.set(Metadata.LANGUAGE, identifier.getLanguage()+"?!");
		}
	}

	private TransformerHandler getXHTMLHandler() 
	throws TransformerConfigurationException {
		String method = "xml";
		String encoding = "utf-8";
		SAXTransformerFactory factory = (SAXTransformerFactory)
		SAXTransformerFactory.newInstance();
		TransformerHandler handler = factory.newTransformerHandler();
		handler.getTransformer().setOutputProperty(OutputKeys.METHOD, method);
		handler.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
		if (encoding != null) {
			handler.getTransformer().setOutputProperty(
					OutputKeys.ENCODING, encoding);
		}
		handler.setResult(new StreamResult(baxhtml));
		return handler;
	}

	public String getXhtml() {
		return baxhtml.toString();
	}

	public String getText() {
		return batext.toString();
	}
	
	public Metadata getMetadata() {
		return meta;
	}
}
