package servlets;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.mail.MailService.Attachment;
import com.google.appengine.api.mail.MailService.Message;
import com.google.common.base.Charsets;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import entities.ListOfReports;
import entities.Logs;
import entities.StatusReport;

//sends email to customers and homebase, and removes c50,as we only need to send the C50 once
//should also email all the C50 removed to us so we know what to invoice
public class Emailer extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {


		emailStatusReport();
		removeC50();

		Logs l = new Logs("email sent");
		ObjectifyService.ofy().save().entity(l);

	}

	//sends the status report i have to created
	public void emailStatusReport()
	{

		// Recipient's email ID needs to be mentioned.
		String to = "jake.labelle@hotmail.co.uk";

		// Sender's email ID needs to be mentioned
		String from = "admiralpork@googlemail.com";
		try{    
			Message msg = new Message();
			msg.setSender(from);
			msg.setTo(to);
			msg.setSubject("HomeBase Status Report");
			Attachment atch = new Attachment("CanadianSpaCompany.HB1", createHB1().getBytes(Charsets.UTF_16));
			msg.setAttachments(atch);
			MailService ms = MailServiceFactory.getMailService();
			ms.send(msg);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}




	}

	//String which created HB1 depending on list of reports
	public String createHB1()
	{
		return createHeader() + createBody() + createTail();

	}

	//header as said by the homebase supplier pdf
	public String createHeader()
	{

		String head="";
		head += "HDR";
		head += "0001";
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		Date date = new Date();
		head += dateFormat.format(date);
		head += "1600";
		head += "00000000";
		head += "Canadian-Spa-Company";
		head += "\r\n";
		return head;

	}

	//this goes through each report, and creates a line of its information
	public String createBody()
	{
			String body ="";
			Key<ListOfReports> theBook = Key.create(ListOfReports.class, "default");

			List<StatusReport> listOfReports = ObjectifyService.ofy()
					.load()
					.type(StatusReport.class) 
					.ancestor(theBook)
					.list();// Anyone in this book			
			for(StatusReport s : listOfReports )
			{
				body += "001";
				body += s.orderNumber;
				body += s.status;
				body += s.transactionDate.substring(0, 6) + s.transactionDate.substring(8, 10);
				body += "1600";
				body += s.originalCustomerOrderNumber;
				body += "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
				body += "\r\n";
			}

			return body;

	}

	//tail as said by the homebase supplier pdf
	public String createTail()
	{
		String tail="";
		tail += "TLR";
		tail += "0001";
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		Date date = new Date();
		tail += dateFormat.format(date);
		tail += "1600";
		tail += "00000000";
		tail += "Canadian-Spa-Company";
		return tail;



	}

	//this removes all c50 from the status report
	public void removeC50()
	{
			Key<ListOfReports> theBook = Key.create(ListOfReports.class, "default");

			List<StatusReport> listOfReports = ObjectifyService.ofy()
					.load()
					.type(StatusReport.class) 
					.ancestor(theBook)
					.list();// Anyone in this book
			for(StatusReport s : listOfReports )
			{
				if (s.status.equals("C50"))
				{
					ObjectifyService.ofy().delete().entities(s);
					Logs l = new Logs("removed status report " + s.orderNumber +  "\r\n");
					ObjectifyService.ofy().save().entity(l);
				}
			}
			

	}
}
