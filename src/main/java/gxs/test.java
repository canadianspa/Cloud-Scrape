package gxs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.appengine.api.datastore.KeyFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

public class test extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String orderNumber,transactionDate,originalCustomerOrderNumber;
		ArrayList<String> quanity,articleCodes,price,tax;
		Customer customer;
		orderNumber = "10";
		transactionDate = "13/12/17";
		originalCustomerOrderNumber = "3";
		quanity = new ArrayList<String>();
		quanity.add("1");
		articleCodes = new ArrayList<String>();
		articleCodes.add("432922");
		price = new ArrayList<String>();
		price.add("5");
		tax = new ArrayList<String>();
		tax.add("0.2");
		customer = new Customer("3","4","5","6","7","8","","9","United Kingdom","10","11");
		StatusReport s;
		try {
			s = new StatusReport(orderNumber,transactionDate,originalCustomerOrderNumber,articleCodes,quanity,price,tax,customer);
		    Key<ListOfReports> theBook = Key.create(ListOfReports.class, "default");
			ObjectifyService.ofy().save().entity(s);

			List<StatusReport> reports = ObjectifyService.ofy()
			          .load()
			          .type(StatusReport.class) // We want only Greetings
			          .ancestor(theBook)
			          .list();// Anyone in this book
			
		  

			for(StatusReport sr: reports)
			{
				resp.getWriter().write(originalCustomerOrderNumber);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			resp.getWriter().write(e.getMessage());
		}


	}

}


