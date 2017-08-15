package gxs;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.mail.MailService.Attachment;
import com.google.appengine.api.mail.MailService.Message;
import com.google.common.base.Charsets;

//sends email to customers and homebase, and removes c50
public class Emailer extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		
		emailStatusReport();
		removeC50();
		sharedInformation.logs += "Email sent \r\n";

	}
	
	
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

	public String createBody()
	{
		String body ="";
		for(StatusReport s : sharedInformation.currentStatusReports)
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
	
	public void removeC50()
	{
		Iterator<StatusReport> itr = sharedInformation.currentStatusReports.iterator();
		while(itr.hasNext()){
		    StatusReport cStatusReport = itr.next();
		    if (cStatusReport.status.equals("C50"))
		    {
		    	itr.remove();
		    }
		}
	}
}
