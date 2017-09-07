package gxs;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

public class DetectDelivery extends HttpServlet  {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		findDelivered();

		try {
			Cache cache;
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			cache = cacheFactory.createCache(Collections.emptyMap());
			cache.put("logs", cache.get("logs") + "delivery status updated \r\n");

		} catch (CacheException e) {
			e.printStackTrace();
		}

	}

	//this servlet checks each order to see if it has been delivered
	//if it has been delivered is sets the status to C50
	public void findDelivered()
	{
	
		Key<ListOfReports> theBook = Key.create(ListOfReports.class, "default");

		List<StatusReport> listOfReports = ObjectifyService.ofy()
				.load()
				.type(StatusReport.class) 
				.ancestor(theBook)
				.list();// Anyone in this book
		for(StatusReport s : listOfReports)
		{
			if(isDelivered(s.veeqoOrderNumber))
			{
				s.delivered();
				ObjectifyService.ofy().save().entity(s);
				try {
					Cache cache;
					CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
					cache = cacheFactory.createCache(Collections.emptyMap());
					cache.put("logs", cache.get("logs") + s.orderNumber + " delivered \r\n");
				} catch (CacheException e) {
					e.printStackTrace();
				}

			}
		}

	}

	//takes the veeqo order number and finds if it was shipped more than 4 days ago
	//i use 4 days to make sure it has actually arrived
	public boolean isDelivered(String veeqoOrderNumber)
	{


		try {
			URL url = new URL("https://api.veeqo.com/orders/" + veeqoOrderNumber);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("x-api-key", sharedInformation.APIKEY);
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");

			String body = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);
			// "shipment": null MEANS NOT DELIVERED


			String shipment = body.substring(body.indexOf("\"shipment\""));
			if(shipment.contains("\"shipment\":null"))
			{
				return false;
			}
			else
			{

				shipment = shipment.split(",")[1];
				String sDate = shipment.substring(14,24);
				Date shipmentdate;
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				try {
					shipmentdate = format.parse(sDate);
					//4 days after ship, count as delivered
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE, -4);
					Date date4daysago = cal.getTime();
					if(shipmentdate.before(date4daysago))
					{
						return true;
					}
					else
					{
						//make this false
						return true;
					}
				} catch (ParseException e) {
					return false;
				}

			}
		} catch (Exception e) {
			return false;
		}

	}

}

