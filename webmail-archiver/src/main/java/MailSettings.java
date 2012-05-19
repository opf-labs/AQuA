import java.util.ArrayList;
import java.util.List;

/**
 * Class to define the parameters needed for the known providers 
 * 
 */
public class MailSettings {

	public final static MailSettings[] settings = new MailSettings[] {
			new MailSettings("YAHOO", "pop.mail.yahoo.com", 995, "pop3s"),
			new MailSettings("GOOGLE", "pop.gmail.com", 995, "pop3s"),
			new MailSettings("GOOGLE_IMAP", "imap.gmail.com", 993, "imaps"),
			new MailSettings("YAHOO_IMAP", "imap.mail.yahoo.com", 993, "imaps"),
			new MailSettings("HOTMAIL", "pop3.live.com", 995, "pop3s"),
            // new MailSettings("MAIL_MRPOSTMAN", "127.0.0.1", 11110, "pop3"),
	};
   // Mobile version of GMAIL : http://mail.google.com/mail/?ui=mobile

	private String name;
	private String host;
	private int port;
	private String protocol; // one of pop3, pop3s, imap, imaps

	public static String[] getProviders() {
		List<String> prov = new ArrayList<String>();
		for (MailSettings pop : settings) {
			prov.add(pop.getName());
		}
		String[] ps = new String[prov.size()];
		prov.toArray(ps);
		return ps;
	}

	public static MailSettings getSettings(String name) {
		for (MailSettings pop : settings) {
			if (pop.getName().equals(name)) {
				return pop;
			}
		}
		return null;
	}

	private MailSettings(String name, String host, int port, String protocol)  {
		this.name = name;
		this.host = host;
		this.port = port;
		this.protocol = protocol;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

}
