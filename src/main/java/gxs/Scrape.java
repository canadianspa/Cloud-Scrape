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

//scrapes gxs of all homebase order abnd creates status reports
public class Scrape extends HttpServlet {

	int pageOn;
	int placeOn;
	String poToGoTo;
	boolean seenFirst = false;
	boolean atLastFound = false;
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		scrapeGSX();

	}

	public void scrapeGSX()
	{
		poToGoTo = sharedInformation.firstPoSeen;
		atLastFound = false;
		seenFirst = false;
		pageOn = 1;
		placeOn = 0;
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
								sharedInformation.currentStatusReports.add(add);
								sharedInformation.logs += "added report " + add.orderNumber + "\r\n";
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
			sharedInformation.firstPoSeen = orderNumber;
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
		String company = "Homebase";

		Customer c = new Customer(telephone,mobile,fname,name,company,addr1,"",city,"United Kingdom",region,postCode);
		
		String closerToOrder = html.substring(html.indexOf("CASE COST"), html.indexOf("END OF ORDER"));
		String[] orderSplit = closerToOrder.split("<!--EAE: please ignore, needed for variable storage -->");
		
		ArrayList<String> articleCodes = new ArrayList<String>();
		ArrayList<String> quantity = new ArrayList<String>();
		ArrayList<String> price = new ArrayList<String>();
		ArrayList<String> tax = new ArrayList<String>();
		
		articleCodes.add(orderSplit[0].split("<td")[9].substring(orderSplit[0].split("<td")[9].indexOf(">") + 1, orderSplit[0].split("<td")[9].indexOf("<")));
		quantity.add(orderSplit[0].split("<td")[11].substring(orderSplit[0].split("<td")[11].indexOf(">") + 1, orderSplit[0].split("<td")[11].indexOf("<")));
		price.add(orderSplit[0].split("<td")[12].substring(orderSplit[0].split("<td")[12].indexOf(">") + 1, orderSplit[0].split("<td")[12].indexOf("<")));
		tax.add("0.2");
		
		for(int i = 1; i < orderSplit.length - 1;i ++)
		{
			articleCodes.add(orderSplit[i].split("<td")[3].substring(orderSplit[0].split("<td")[9].indexOf(">") + 1, orderSplit[0].split("<td")[9].indexOf("<")));
			quantity.add(orderSplit[i].split("<td")[5].substring(orderSplit[0].split("<td")[11].indexOf(">") + 1, orderSplit[0].split("<td")[11].indexOf("<")));
			price.add(orderSplit[0].split("<td")[6].substring(orderSplit[0].split("<td")[12].indexOf(">") + 1, orderSplit[0].split("<td")[12].indexOf("<")));
			tax.add("0.2");
		}
		
		return new StatusReport(orderNumber,transactionDate,originalCustomerOrderNumber,articleCodes,quantity,price,tax,c);

	}

	
	
	

}


