package servlets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.googlecode.objectify.ObjectifyService;

import entities.BNQPurchaseOrder;
import entities.Customer;
import entities.EDA;
import entities.Logs;
import entities.StatusReport;
//scrapes gxs of all bnq order abnd creates eda
public class BNQScrape extends HttpServlet {

	int pageOn;
	int placeOn;
	String poToGoTo;
	boolean seenFirst = false;
	boolean atLastFound = false;
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		scrapeGSX();


	}

	public void scrapeGSX()
	{

	
		//not at the last found order yet
		atLastFound = false;
		//becomes true the first time it sees a order
		seenFirst = false;
		//start at the first item on gsx
		pageOn = 1;
		placeOn = 0;
		try 
		{
			//keep scraping until you reach the first order it saw last scrape
	        poToGoTo = ObjectifyService.ofy().load().type(BNQPurchaseOrder.class).list().get(0).poNumber;
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

			//go the the gxs website
			HtmlPage page = webClient.getPage("https://gxstradeweb.gxsolc.com/pub-log/login.html?lang=en");
			//logs in 
			page = (HtmlPage) page.executeJavaScript("form.User.value='***REMOVED***'; form.Password.value='***REMOVED***';form.submit.click();").getNewPage();
			while(!atLastFound)
			{
				while(placeOn < 10 && !atLastFound)
				{
					System.out.println(pageOn);
					System.out.println(placeOn);
					//goes the inbox (uses the page to make sure on the right page)
					page = webClient.getPage("https://gxstradeweb.gxsolc.com/edi-bin/EdiMailbox.pl?NextDocListed=Next&LastStartNum=" + (((pageOn -2)*10)+1) + "&box_type=in&lang=en&sort_var=");
					//goes the right place
					page = (HtmlPage) page.executeJavaScript("window.open('https://gxstradeweb.gxsolc.com' + form.ReadUrl" + placeOn + ".value,'_self')").getNewPage();		
					//goes to the order
					page = (HtmlPage) page.executeJavaScript("window.open(MAINAREA.location,'_self')").getNewPage();	
					//reads the html from the order
					String html = (String) page.executeJavaScript("document.documentElement.outerHTML").getJavaScriptResult();		
					try {
					//makes sure its a homebase order
					if(isBNQ(html))
					{
						//skip over store orders
					
							EDA add = readBNQHTML(html);
							if(add.purchOrNo.equals(poToGoTo))
							{
								//reached the latest order added before
								atLastFound = true;
							}
							else
							{
								Logs l = new Logs("added report " + add.orderNumber);
								ObjectifyService.ofy().save().entity(l);
								add.uploadOrder();

							}
						
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					//keep adding one to place to go to the next order
					placeOn += 1;
				}
				//at place 10 change it to 0 and go up a page
				placeOn = 0;
				pageOn += 1;

			}

			webClient.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public boolean isBNQ(String html)
	{
		return html.contains("BnQ");
	}
	//takes certain webpage as a html and ouputs bnq stuff
	public EDA readBNQHTML(String html) throws Exception
	{
		//System.out.println(html);
		String storeCode,purchOrderNo,custTellNo1,bQSuppNo,custName;
		ArrayList<String> eanCode1 = new ArrayList<String>();
		ArrayList<String> desc1 = new ArrayList<String>();
		ArrayList<String> qty1 = new ArrayList<String>();
		String dateOrderPlaced, delDate;
		String salesNumber;
		String custAdd1,custAdd2,custAdd3,custAdd4,custPostCode;

		storeCode = html.substring(html.indexOf("hfStoreLocCode") + 25, html.indexOf(">", html.indexOf("hfStoreLocCode")+25)-1);
		purchOrderNo = html.substring(html.indexOf("PURCHASE ORDER NO") + 162, html.indexOf("&", html.indexOf("PURCHASE ORDER NO")+162));
		custTellNo1 = html.substring(html.indexOf("PHONE-DAY") + 166, html.indexOf("&", html.indexOf("PHONE-DAY")+166));
		bQSuppNo = html.substring(html.indexOf("VENDOR") + 155, html.indexOf("&", html.indexOf("VENDOR")+155));
		custName = html.substring(html.indexOf("ADDRESS - HOME DELIVERY") + 183, html.indexOf("&", html.indexOf("ADDRESS - HOME DELIVERY")+183));
		dateOrderPlaced = html.substring(html.indexOf("PURCHASE ORDER DATE") + 164, html.indexOf("&", html.indexOf("PURCHASE ORDER DATE")+164));
		dateOrderPlaced = dateOrderPlaced.replace(".", "/");
		delDate = html.substring(html.indexOf("DELIVERY DATE") + 158, html.indexOf("&", html.indexOf("DELIVERY DATE")+158));
		delDate = delDate.replace(".", "/");
		String custDetail = html.substring(html.indexOf("ADDRESS - HOME DELIVERY"), html.indexOf("CONTACT"));
		salesNumber = html.substring(html.indexOf("SALES ORDER NO") + 160, html.indexOf("&", html.indexOf("SALES ORDER NO")+160));
		String[] custDetailSplit = custDetail.split("<tr>");
		String[] custInfo = new String[5]; 


		//all the actual information 
		for(int i = 2; i < 7; i++)
		{
			custInfo[i -2] = custDetailSplit[i].substring(103, custDetailSplit[i].indexOf("&",103));
		}

		//if the information is empty shift postcode
		int numOfAddrLines = 4;
		for(int i = 4;i >= 0; i--)
		{
			if(custInfo[i].equals(""))
			{
				numOfAddrLines = i - 1;
			}
		}

		if(numOfAddrLines == 0)
		{
			custAdd1 = "";
			custAdd2 = "";
			custAdd3 = "";
			custAdd4 = "";
			custPostCode = custInfo[0];
		}
		else if(numOfAddrLines == 1)
		{
			custAdd1 = custInfo[0];
			custAdd2 = "";
			custAdd3 = "";
			custAdd4 = "";
			custPostCode = custInfo[1];
		}
		else if(numOfAddrLines == 2)
		{
			custAdd1 = custInfo[0];
			custAdd2 = custInfo[1];
			custAdd3 = "";
			custAdd4 = "";
			custPostCode = custInfo[2];
		}
		else if(numOfAddrLines == 3)
		{
			custAdd1 = custInfo[0];
			custAdd2 = custInfo[1];
			custAdd3 = custInfo[2];
			custAdd4 = "";
			custPostCode = custInfo[3];
		}
		else if(numOfAddrLines == 4)
		{
			custAdd1 = custInfo[0];
			custAdd2 = custInfo[1];
			custAdd3 = custInfo[2];
			custAdd4 = custInfo[3];
			custPostCode = custInfo[4];
		}
		else
		{
			System.out.println("No Address Lines?");
			custAdd1 = "";
			custAdd2 = "";
			custAdd3 = "";
			custAdd4 = "";
			custPostCode = "";
		}

		//need to split by <!-- Begin Detail Line -->

		String[] lines = html.split("<!-- Begin Detail Line -->");
		for(int i = 1; i < lines.length; i ++)
		{
			eanCode1.add(lines[i].substring(lines[i].indexOf("EA&n") + 90, lines[i].indexOf("&", lines[i].indexOf("EA&n")+90))); 
			desc1.add(lines[i].substring(lines[i].indexOf("EA&n") + 333, lines[i].indexOf("&", lines[i].indexOf("EA&n")+333)));
			qty1.add(lines[i].substring(191, lines[i].indexOf("&",191)));

		}

		return new EDA(String.valueOf(seqNo), storeCode,  purchOrderNo,  custTellNo1, bQSuppNo,custName,  eanCode1,  desc1,  qty1,dateOrderPlaced,delDate,custAdd1,custAdd2,custAdd3,custAdd4,custPostCode,salesNumber);


	}
	





}


