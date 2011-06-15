/**
 * 
 */
package uk.bl.dpt.aqua;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.DirectoryWalker;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import uk.bl.dpt.api.ByteSequence;
import uk.bl.dpt.entities.ByteSequenceFactory;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a> <a
 *         href="http://sourceforge.net/users/carlwilson-bl">carlwilson-bl AT
 *         SourceForge</a> <a
 *         href="https://github.com/carlwilson-bl">carlwilson-bl AT github</a>
 * @version 0.1
 */
public class InventoryWorkflow extends DirectoryWalker {
	private static final String HELP_OPT = "help";
	private static final String HELP_OPT_DESC = "print this message";
	private static final String SUMMARY_OPT = "sum";
	private static final String SUMMARY_OPT_DESC = "add a summary to the output";
	private static final String DIR_OPT = "dir";
	private static final String DIR_OPT_ARG = "directory";
	private static final String DIR_OPT_DESC = "directory to analyse";
	private static final String[] CSV_HEADER = { "Num", "Path", "Name",
			"Length", "Modified", "sha-256", "Format", "parseproblem" };
	private static List<String[]> RECORDS = new ArrayList<String[]>();

	private long fileCount = 0L;
	private long byteCount = 0L;

	private static Options OPTIONS = new Options();
	static {
		Option help = new Option(HELP_OPT, HELP_OPT_DESC);
		Option summary = new Option(SUMMARY_OPT, SUMMARY_OPT_DESC);
		@SuppressWarnings("static-access")
		Option dir = OptionBuilder.withArgName(DIR_OPT_ARG).hasArg()
				.withDescription(DIR_OPT_DESC).create(DIR_OPT);
		OPTIONS.addOption(help);
		OPTIONS.addOption(summary);
		OPTIONS.addOption(dir);
	}

	private File root = null;
	private Directory dir = null;
	private Analyzer analysis = null;
	private IndexWriter idx = null;
	
	private Map<String, Integer> formatCount = new HashMap<String, Integer>();

	/**
	 * @param rootDir
	 *            the root dir for the crawl
	 * @param beQuiet
	 *            workflowdoesn't output if this is trye
	 */
	public InventoryWorkflow(File rootDir, boolean beQuiet) {
		this.root = rootDir;
		try {
			this.dir = FSDirectory.open(new File("/data/index"));
			// The Version.LUCENE_XX is a required constructor argument in
			// Version 3.
			analysis = new StandardAnalyzer(Version.LUCENE_30);
			// IndexWriter will intelligently open an index for appending if the
			// index directory exists, else it will create a new index
			// directory.
			idx = new IndexWriter(dir, analysis, true,
					IndexWriter.MaxFieldLength.UNLIMITED);
			// walk the directory treee
			this.walk(this.root, null);
			idx.close();
		} catch (IOException e) {
			// We've failed IO wise somewhere
			System.err.println("IO Exception walking directory. Message: "
					+ e.getMessage());
			for (StackTraceElement element : e.getStackTrace()) {
				System.err.println(element.toString());
			}
			e.printStackTrace();
		}
	}

	/**
	 * @return the number of files processed
	 */
	public long getFileCount() {
		return this.fileCount;
	}

	/**
	 * @return the number of bytes processed
	 */
	public long getByteCount() {
		return this.byteCount;
	}

	@Override
	protected boolean handleDirectory(File directory, int depth,
			@SuppressWarnings("rawtypes") Collection results) {
		return true;
	}

	@Override
	protected void handleFile(File file, int depth,
			@SuppressWarnings("rawtypes") Collection results) {
		// Increment the files and bytes
		this.fileCount++;
		this.byteCount += file.length();
		boolean parseProblem = false;

		// OK lets create the byte sequence
		ByteSequence bs = null;
		Metadata metadata = new Metadata();
		ContentHandler handler = new BodyContentHandler(-1);
		try {
			bs = ByteSequenceFactory.createByteSequence(file);
			InputStream stream = new FileInputStream(file);
			ParseContext context = new ParseContext();
			DefaultDetector detector = new DefaultDetector();
			AutoDetectParser parser = new AutoDetectParser(detector);
			context.set(Parser.class, parser);

			parser.parse(stream, handler, metadata, context);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			parseProblem = true;
		}
		File parent = file.getParentFile();
		String mimeString = metadata.get("Content-Type");
		if (this.formatCount.containsKey(mimeString)) {
			Integer count = new Integer (this.formatCount.get(mimeString) + 1);
			this.formatCount.put(mimeString, count);
		} else {
			this.formatCount.put(mimeString, new Integer(1));
		}
		// Add record to csv list for processing
		RECORDS.add(new String[] { String.valueOf(this.fileCount),
				this.root.toURI().relativize(parent.toURI()).toString(),
				file.getName(), String.valueOf(file.length()),
				new Date(file.lastModified()).toString(), bs.getSHA256Digest(),
				mimeString,
				String.valueOf(parseProblem) });
		
		// Create a lucene document for indexing
		Document doc = new Document();
		doc.add(new Field("sha-256", bs.getSHA256Digest(), Field.Store.YES,
				Field.Index.NO));
		doc.add(new Field("name", file.getName(), Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		doc.add(new Field("path", this.root.toURI().relativize(parent.toURI())
				.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("mimetype",
				mimeString, Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		for (int i = 0; i < metadata.names().length; i++) {
			String name = metadata.names()[i];
			doc.add(new Field(metadata.names()[i], metadata.get(name), Field.Store.YES,
					Field.Index.NO));
		}
		doc.add(new Field("contents", handler.toString(), Field.Store.NO,
				Field.Index.ANALYZED));
		try {
			idx.addDocument(doc);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	public void outputReport() {
		StringWriter strWriter = new StringWriter();
		CSVWriter writer = new CSVWriter(strWriter, ',');
		// feed in your array (or convert your data to an array)
		writer.writeNext(CSV_HEADER);
		for (String[] reportLine : RECORDS) {
			writer.writeNext(reportLine);
		}
		System.out.println(strWriter.getBuffer().toString());
		
		for (String mimeType : this.formatCount.keySet()) {
			System.out.println(mimeType + ":" + this.formatCount.get(mimeType));
		}
	}

	/**
	 * @param args
	 *            the command line args to the workflow
	 */
	public static void main(String[] args) {
		// Create a command line parser
		CommandLineParser cmdParser = new GnuParser();
		try {
			// Parse the command line arguments
			CommandLine cmd = cmdParser.parse(OPTIONS, args);
			// Check for help option, or no args
			if ((cmd.hasOption(HELP_OPT)) || (cmd.getOptions().length < 1)
					|| (!cmd.hasOption(DIR_OPT))) {
				// OK help found, or no args so print help and exit
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("inventory", OPTIONS);
				System.exit(0);
			}

			// Now check that the passed dir is a directory
			File root = new File(cmd.getOptionValue(DIR_OPT));
			if ((!root.exists()) || (!root.isDirectory())) {
				System.out.println("-dir value " + cmd.getOptionValue(DIR_OPT)
						+ " should be an existing directory.");
				System.exit(0);
			}
			System.out.println("Started:" + new Date().toString());
			InventoryWorkflow wrkflw = new InventoryWorkflow(root, true);
			wrkflw.outputReport();
		} catch (ParseException e) {
			// Ooops, parsing commands went wrong
			System.err.println("Command parsing failed.  Reason: "
					+ e.getMessage());
		}
	}
}