import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;

import org.apache.log4j.Logger;
import org.archive.io.arc.ARCWriter;
import org.archive.util.ArchiveUtils;

/**
 * Class to harvest a mail account in one of the provider (either pop3 or imap).
 * The collected mails are then stored in a arc file 
 * 
 * Coming from
 *  http://java.sun.com/developer/onlineTraining/JavaMail/contents.html#JavaMailIntro
 * Reading of mail directly inspired from the demo : msgshow.java 
 *
 * Introduction to mail can be found at http://en.wikipedia.org/wiki/MIME
 * 
 */
public class WebMail {
    static Logger LOG = Logger.getLogger(WebMail.class);

    /**
     * Tells whether we want compressed arcs or ordinary ones.
     */
    private static boolean ARC_COMPRESSED = false;

    /**
     * Tells where to generates the archives
     */
    private File storageDir = new File(System.getProperty("java.io.tmpdir"));

	
    /**
     * Variables to handle the exchange with the mail provider
     */
    
    private Session session = null;
	private Store store = null;
	private String username, password;
	private MailSettings mailSettings;
	private Folder folder;

    /**
     * Variables to handle the creation of the arc file
     */
    
    private File arcFile;
    private String hostIp;
	
	/**
	 * Default constructor
	 */
	public WebMail() {

	}

	/**
	 * Setter for user and password information
	 */
	public void setUserPass(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	
	/**
	 * Setter for the information about a specific provider
	 * @param settings
	 */
	public void setMailSettings(MailSettings settings) {
		this.mailSettings = settings;
	}

	/**
	 * Getter to the arc file generated
	 * @return the arcFile
	 */
	public File getArcFile() {
		return arcFile;
	}
	
	public void connect() throws Exception {
		Properties props = new Properties();

		session = Session.getDefaultInstance(props, null);
		store = session.getStore(mailSettings.getProtocol());
		store.connect(mailSettings.getHost(), mailSettings.getPort(), 
				username, password);
		
	}

	public void openFolder(String folderName) throws MessagingException {
		// Make sure that for pop3 
        if ( mailSettings.getProtocol() == null || 
        	 mailSettings.getProtocol().startsWith("pop3")
        ) {
        	folderName = "INBOX";
        }
        
        // Open the Folder
        folder = store.getDefaultFolder();
        
        folder = folder.getFolder(folderName);        
        if (folder == null) {
            throw new MessagingException("Invalid folder");
        }
        // try to open read-only
        folder.open(Folder.READ_ONLY);
    }
    
    public void closeFolder() throws Exception {
        folder.close(false);
    }
    
    public int getMessageCount() throws Exception {
        return folder.getMessageCount();
    }
    
    public int getNewMessageCount() throws Exception {
        return folder.getNewMessageCount();
    }
    
    public void disconnect() throws Exception {
        store.close();
    }
    
    public void setArchive(String prefixe) {
    	StringBuffer sb = new StringBuffer();
    	sb.append(prefixe).append("_");
    	sb.append(this.username.replaceAll("@", "_at_"));
    	sb.append("_").append(Long.toString(System.currentTimeMillis() / 1000L));
    	sb.append(".arc");
    	if (ARC_COMPRESSED) {
    		sb.append(".gz");
        }
    	this.arcFile = new File(storageDir, sb.toString());
    	
        // Retrieve the IP
        try {
            InetAddress ip = InetAddress.getLocalHost();
            this.hostIp =  ip.getHostAddress();
        } catch (UnknownHostException e) {
            this.hostIp = "127.0.0.1";
        }
   }
    
	public void dumpAllMessages() throws Exception {
    	// Initialize the arc writer
        AtomicInteger ai = new AtomicInteger(1);
        PrintStream ps = new PrintStream(new FileOutputStream(this.arcFile));
        String now = ArchiveUtils.get14DigitDate();
        List<?> lmeta = null;
      
        
        // Attributes & Flags for all messages ..
        Message[] msgs = folder.getMessages();
        if (msgs.length == 0) {
        	LOG.warn("No messages to archive !!!! ");
        	return;
        }
        ARCWriter arcWriter = new ARCWriter(
                ai, ps, arcFile, ARC_COMPRESSED, now, lmeta);
        String prefixeURL = "mailto://" + this.username;
        
        // Use a suitable FetchProfile
        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.ENVELOPE);        
        folder.fetch(msgs, fp);
        
        
        for (int i = 0; i < msgs.length; i++) {
        // for (int i = 0; i < 1; i++) {
            LOG.info("MESSAGE #" + (i + 1) + ":");
            dumpInitialMessage(msgs[i], arcWriter, prefixeURL);
        }
        
        arcWriter.close();
    }
    
	public void dumpInitialMessage(Part p, ARCWriter arcWriter, String prefixeURL) throws MessagingException, IOException {
    	String uri = prefixeURL;
    	long fetchBeginTimeStamp = System.currentTimeMillis();
        Message m = null;
        if (!(p instanceof Message)) {
        	return;
        }
        m = (Message)p;
        
    	LOG.info("SUBJECT: " + m.getSubject());
    	uri = prefixeURL + "?subject=" + URLEncoder.encode(m.getSubject(), "UTF-8"); 
    	Date d = m.getReceivedDate();
    	if (d  != null) {
    		fetchBeginTimeStamp = d.getTime();
    	} else {
    		d = m.getSentDate();
    		if (d != null) {
    			fetchBeginTimeStamp = d.getTime();
    		} else {
    			LOG.warn("Not date for this message take now");
    		}
    	}
    	dumpHeader(m, fetchBeginTimeStamp, arcWriter, uri);
        dumpPart(p, fetchBeginTimeStamp, arcWriter, uri);
	}

	public void dumpHeader(Message m, long receiveDate, ARCWriter arcWriter, String prefixeURL) throws MessagingException, IOException {
        String uri = prefixeURL;
        String contentType = null;
        try {
        	contentType = m.getContentType(); // "message/rfc822"
        } catch (MessagingException e) {
        	contentType = "message/rfc822";
        }
        Enumeration<?> e = m.getAllHeaders();
        
        StringBuffer sb = new StringBuffer();
		while (e.hasMoreElements()) {
			Header h = (Header)e.nextElement();
			sb.append(h.getName()).append(": ").append(h.getValue()).append("\n");
		}
    	String s = sb.toString();
    	byte[] b = s.getBytes("UTF-8");
    	long recordLength = b.length;
    	InputStream in = new ByteArrayInputStream(b);
    	arcWriter.write(uri, contentType, this.hostIp, receiveDate, recordLength, in, true);
    	LOG.info("---------------------------");
		
        
	}
	public void dumpPart(Part p, long receiveDate, ARCWriter arcWriter, String prefixeURL) throws MessagingException, IOException {
       String uri = prefixeURL;
        String contentType;
        try {
        	contentType = p.getContentType();
        } catch (MessagingException e) {
        	contentType = "no-type";
        }
        LOG.info("dumpPart CONTENT-TYPE: " + contentType);
        try {
            LOG.info("CONTENT-TYPE: " + (new ContentType(contentType)).toString());
        } catch (ParseException pex) {
            LOG.error("BAD CONTENT-TYPE: " + contentType);
            return;
        }
        /*
         * Using isMimeType to determine the content type avoids
         * fetching the actual content data until we need it.
         */
        long recordLength = 0;
        InputStream in = null;
        if (p.isMimeType("multipart/*")) {
        	// Handle multipart and recursion
        	dumpMultipart((Multipart)p.getContent(), contentType, receiveDate, arcWriter, uri);
        } else if (p.isMimeType("message/rfc822")) {
			LOG.info("This is a Nested Message");
			dumpPart((Part) p.getContent(), receiveDate, arcWriter, uri);
        } else if (p.isMimeType("text/*")) {
        	// load in memory to know the size
        	String charset = extractCharset(contentType);
        	String s = (String)p.getContent();
        	byte[] b = s.getBytes(charset);
        	recordLength = b.length;
        	LOG.info("This is " + contentType + "/" + charset + " of said size " + p.getSize() + " and real size " + recordLength);
        	in = new ByteArrayInputStream(b);
            boolean enforceSize = true;
        	arcWriter.write(uri, contentType, this.hostIp, receiveDate, recordLength, in, enforceSize);
        	LOG.info("---------------------------");
        } else {
        	LOG.info("This is " + contentType + " of size " + p.getSize());
            recordLength = p.getSize();
            in = p.getInputStream();
            // TODO need to serialize to a file to known the REAL size (should be true !!!)
            boolean enforceSize = false;
            arcWriter.write(uri, contentType, this.hostIp, receiveDate, recordLength, in, enforceSize);
        	LOG.info("---------------------------");
        }
        if (in != null) in.close();
    }

	
    /**
     * Extract the charset from a contentType string like
     *    text/plain; charset=ISO-8859-1
     * @param ct the content type string
     * @return a charset
     */
    public String extractCharset(String ct) {
    	for (String s : ct.split("; ")) {
    		// LOG.info(s);
    		if (s.startsWith("charset=")) {
    			return s.substring(8).replaceAll("\"", "");
    		}
    	}
    	return "UTF-8";
    }
    
	public void dumpMultipart(Multipart mp, String contentType, long receiveDate, ARCWriter arcWriter, String prefixeURL) throws MessagingException, IOException  {

		for (int i = 0, n = mp.getCount(); i < n; i++) {
			Part part = mp.getBodyPart(i);
			
	    	String uri = prefixeURL;
	    	long fetchBeginTimeStamp = receiveDate;
	        long recordLength = 0;
	       
	        String ctP = part.getContentType();

			String disposition = part.getDisposition();
			
			if ((disposition != null) &&
				(disposition.equals(Part.ATTACHMENT) || 
				 disposition.equals(Part.INLINE))
			) {
				LOG.info("Part " + (i + 1) + " kind " + disposition + " type " + ctP + " name " + part.getFileName());
				if (part.getFileName() != null) {
					uri = prefixeURL + "#" + URLEncoder.encode(part.getFileName(), "UTF-8");
				} else {
					uri = prefixeURL + "#attachment" + (i + 1);
				}
	            recordLength = part.getSize();
	            InputStream in = part.getInputStream();
	            // TODO need to serialize to a file to known the REAL size (should be true !!!)
	            boolean enforceSize = false;
	            arcWriter.write(uri, contentType, this.hostIp, fetchBeginTimeStamp, recordLength, in, enforceSize);
	            in.close();
				
			}  else {
				if (part.getFileName() != null) {
					uri = prefixeURL + "#" + URLEncoder.encode(part.getFileName(), "UTF-8");
				} else {
					uri = prefixeURL + "#part" + (i + 1);
				}
				LOG.info("Dump part " + (i+1));
				dumpPart(part, fetchBeginTimeStamp, arcWriter, uri);

			}
		}
	}
	
    
	/**
	 * Main function for quick testing
	 * @param args
	 * @throws MessagingException
	 */
	public static void main(String[] args) throws MessagingException {
		  try {
	            
	            WebMail wm = new WebMail();
	            
	            wm.setUserPass("userX@gmail.com", "XXX"); 
	            MailSettings sets = MailSettings.getSettings("GOOGLE_IMAP");
	            if (sets == null) throw new Exception("Unknown");
	            wm.setMailSettings(sets);
	            wm.connect();
	            // Restriction in POP3 only the inbox folder can be looked at
	            wm.openFolder("INBOX");
	            // wm.openFolder("For harvesting");
	            
	            int totalMessages = wm.getMessageCount();
	            
	            LOG.info("Total messages = " + totalMessages);
	            LOG.info("-------------------------------");
	            
	            wm.setArchive("mail");
	            wm.dumpAllMessages();
	            
	            LOG.info("Archive created at " + wm.getArcFile());
	        } catch(Exception e) {
	            LOG.error("Error " + e.getMessage(), e);
	            System.exit(-1);
	        }
	}

}
