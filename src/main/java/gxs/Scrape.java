package gxs;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailService.Attachment;
import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.common.base.Charsets;


public class Scrape extends HttpServlet {

	String api = "***REMOVED***";
	ArrayList<StatusReport> listOfReports = new ArrayList<StatusReport>();
	int pageOn;
	int placeOn;
	String firstPoSeen = "4517002484";
	String poToGoTo;
	boolean seenFirst = false;
	boolean atLastFound = false;
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		scrapeGSX();
		resp.setContentType("text/plain");
		for(StatusReport s : listOfReports)
		{
			resp.getWriter().println(s.orderNumber);
			resp.getWriter().println(s.transactionDate);
			resp.getWriter().println(s.originalCustomerOrderNumber + System.lineSeparator());

		}

		resp.getWriter().println(listOfReports.size());

	}

	public void scrapeGSX()
	{
		poToGoTo = firstPoSeen;
		atLastFound = false;
		seenFirst = false;
		pageOn = 2;
		placeOn = 5;
		try 
		{
			WebClient webClient = new WebClient(BrowserVersion.FIREFOX_52);
			webClient.getOptions().setRedirectEnabled(true);
			webClient.getOptions().setJavaScriptEnabled(true);                                             
			webClient.getOptions().setThrowExceptionOnScriptError(false);             
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);  
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setCssEnabled(true);
			webClient.getOptions().setAppletEnabled(true);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.waitForBackgroundJavaScript(10000);

			HtmlPage page = webClient.getPage("https://gxstradeweb.gxsolc.com/pub-log/login.html?lang=en");   
			page = (HtmlPage) page.executeJavaScript("form.User.value='***REMOVED***'; form.Password.value='***REMOVED***';form.submit.click();").getNewPage();
			while(!atLastFound)
			{
				while(placeOn < 10 && !atLastFound)
				{
					System.out.println(pageOn);
					System.out.println(placeOn);
					page = webClient.getPage("https://gxstradeweb.gxsolc.com/edi-bin/EdiMailbox.pl?NextDocListed=Next&LastStartNum=" + (((pageOn -2)*10)+1) + "&box_type=in&lang=en&sort_var=");
					page = (HtmlPage) page.executeJavaScript("window.open('https://gxstradeweb.gxsolc.com' + form.ReadUrl" + placeOn + ".value,'_self')").getNewPage();		
					page = (HtmlPage) page.executeJavaScript("window.open(MAINAREA.location,'_self')").getNewPage();	
					String html = (String) page.executeJavaScript("document.documentElement.outerHTML").getJavaScriptResult();		
					if(isHomeBase(html))
					{
						//skip over store orders
						try {
							StatusReport add = readHomeBaseHTML(html);
							if(add.orderNumber.equals(poToGoTo))
							{
								//reached the latest order added before
								atLastFound = true;
								System.out.println("hey");
							}
							else
							{
								listOfReports.add(add);
							}
						} catch (Exception e) {
						}
					}
					placeOn += 1;
				}
				placeOn = 0;
				pageOn += 1;

			}

			webClient.close();
			emailStatusReport();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public boolean isHomeBase(String html)
	{
		return html.contains("HOMEBASE");
	}
	//takes certain webpage as a html and ouputs homebase stuff
	public StatusReport readHomeBaseHTML(String html) throws Exception
	{


		String orderNumber,transactionDate,originalCustomerOrderNumber;
		orderNumber = html.substring(html.indexOf("END OF ORDER") + 13, html.indexOf("<", html.indexOf("END OF ORDER")+13));
		System.out.println(orderNumber);
		originalCustomerOrderNumber = html.substring(html.indexOf("CUSTOMER ORDER") + 90, html.indexOf("<", html.indexOf("CUSTOMER ORDER")+90)-1);
		System.out.println(originalCustomerOrderNumber);
		transactionDate = html.substring(html.indexOf("DELIVER BY") + 85, html.indexOf("<", html.indexOf("DELIVER BY")+85));
		System.out.println(transactionDate);

		Pattern testPattern= Pattern.compile("^[0-9]{10}");
		Matcher teststring= testPattern.matcher(originalCustomerOrderNumber);

		if(!teststring.matches())
		{

			throw new Exception("no customer order");
		}

		//store first order seen
		if(!seenFirst)
		{
			firstPoSeen = orderNumber;
			seenFirst = true;
		}
		String custDetail = html.substring(html.indexOf("DELIVER TO"), html.indexOf("POST CODE"));

		String[] custDetailSplit = custDetail.split("</td>");

		String fname = custDetailSplit[1].substring(custDetailSplit[1].indexOf(">:") +3, custDetailSplit[1].length());
		String addr1 = custDetailSplit[5].substring(custDetailSplit[5].indexOf(">:") +3, custDetailSplit[5].length());
		String city = custDetailSplit[8].substring(custDetailSplit[8].indexOf(">:") +3, custDetailSplit[8].length());
		String region = custDetailSplit[11].substring(custDetailSplit[11].indexOf(">:")+3 , custDetailSplit[11].length());
		
		String postCode = html.substring(html.indexOf("POST CODE") + 84, html.indexOf("<", html.indexOf("POST CODE")+84));
		String telephone = html.substring(html.indexOf("TELEPHONE") + 84, html.indexOf("<", html.indexOf("TELEPHONE")+84));
		String mobile;
		if (html.indexOf("MOBILE TEL NO.") == -1)
		{
			mobile = "";
		}
		else
		{
			mobile = html.substring(html.indexOf("MOBILE TEL NO.") + 89, html.indexOf("<", html.indexOf("MOBILE TEL NO.")+89));

		}

		String name = html.substring(html.indexOf("CUSTOMER NAME") + 78, html.indexOf("<", html.indexOf("CUSTOMER NAME")+78));
		name = name.substring(name.indexOf(" ") + 1,name.length());
		String email = "placeholder@email.com";
		String company = "placeholder";

		Customer c = new Customer(email,telephone,mobile,fname,name,company,addr1,"",city,"United Kingdom",postCode);
		
		String closerToOrder = html.substring(html.indexOf("CASE COST"), html.indexOf("END OF ORDER"));
		String[] orderSplit = closerToOrder.split("<!--EAE: please ignore, needed for variable storage -->");
		
		ArrayList<String> articleCodes = new ArrayList<String>();
		ArrayList<String> quantity = new ArrayList<String>();
		
		articleCodes.add(orderSplit[0].split("<td")[9].substring(orderSplit[0].split("<td")[9].indexOf(">") + 1, orderSplit[0].split("<td")[9].indexOf("<")));
		quantity.add(orderSplit[0].split("<td")[11].substring(orderSplit[0].split("<td")[11].indexOf(">") + 1, orderSplit[0].split("<td")[11].indexOf("<")));
		
		for(int i = 1; i < orderSplit.length - 1;i ++)
		{
			articleCodes.add(orderSplit[i].split("<td")[3].substring(orderSplit[0].split("<td")[9].indexOf(">") + 1, orderSplit[0].split("<td")[9].indexOf("<")));
			quantity.add(orderSplit[i].split("<td")[5].substring(orderSplit[0].split("<td")[11].indexOf(">") + 1, orderSplit[0].split("<td")[11].indexOf("<")));

		}
		
		return new StatusReport(orderNumber,transactionDate,originalCustomerOrderNumber,articleCodes,quantity,c);

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
		for(StatusReport s : listOfReports)
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
	
	

}


