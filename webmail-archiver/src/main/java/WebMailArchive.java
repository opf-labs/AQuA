import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class PopArchive
 */
public class WebMailArchive extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger LOG = org.apache.log4j.Logger.getLogger(WebMailArchive.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public WebMailArchive() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String provider = request.getParameter("provider");
		String folder = request.getParameter("folder");
		if (folder == null || folder.length() == 0) {
			folder = "INBOX";
		}

		int totalMessages = 0;
		File arcFile = null;
		boolean bError = false;
		String errMessage = "";

		LOG.info("doPost for " + username + " from " + provider);
		try {

			WebMail wm = new WebMail();
			wm.setUserPass(username, password);
			MailSettings sets = MailSettings.getSettings(provider);
			if (sets == null) {
				throw new IOException("Provider " + provider + " unknown");
			}

			wm.setMailSettings(sets);
			wm.connect();
			wm.openFolder(folder);

			totalMessages = wm.getMessageCount();

			wm.setArchive("mail");
			wm.dumpAllMessages();
			arcFile = wm.getArcFile();
			LOG.info("Archive created at " + wm.getArcFile());
		} catch (Exception e) {
			LOG.error("Error " + e.getMessage(), e);
			errMessage = e.getMessage();
			bError = true;
		}

		if (bError) {
			response.sendError(500, "Error " + errMessage);
			return;
		}

		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Expires", "0");

		// Process the response
		PrintWriter out = new PrintWriter(
				new OutputStreamWriter(response.getOutputStream(), "UTF-8"));

		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>Web mail archive for "
						+ username + "</title>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		out.println("<link rel=\"stylesheet\" href=\"./style.css\" type=\"text/css\" />");
		out.println("</head><body>");

		out.println("<h1>Web mail archive for " + username + " from "
				+ provider + "</h1>");
		out.println("<p>We archive " + totalMessages + " messages from the ["
				+ folder + "] folder</p>");
		out.println("<p>and generate  arc file " + arcFile.getName()
				+ " of size " + arcFile.length() + "</p>");
		out.println("<br /><h2>Thank you for providing this information</h2>");

		out.println("</body></html>");
		out.close();
	}

}
