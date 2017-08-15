package gxs;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class DetectDelivery extends HttpServlet  {
	String APIKEY = "***REMOVED***";

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		findDelivered();
		sharedInformation.logs += "delivery status updated";

	}

	public void findDelivered()
	{
		for(StatusReport s : sharedInformation.currentStatusReports)
		{
			if(isDelivered(s.veeqoOrderNumber))
			{
				s.delivered();
				sharedInformation.logs += s.orderNumber + " delivered";
			}
		}
	}

	public boolean isDelivered(String veeqoOrderNumber)
	{
		String APIKEY = "***REMOVED***";
		Client client = ClientBuilder.newClient();
		Response response = client.target("https://api.veeqo.com/orders/" + veeqoOrderNumber )
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("x-api-key", APIKEY)
				.get();

		//      "shipment": null MEANS NOT DELIVERED
		String body = response.readEntity(String.class);

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
					return false;
				}
			} catch (ParseException e) {
				return false;
			}

		}
	}

}

