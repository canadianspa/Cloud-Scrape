package gxs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.repackaged.com.google.gson.JsonObject;


public class toVeeqoServer extends HttpServlet {

	String APIKEY = "***REMOVED***";
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


Client client = ClientBuilder.newClient();
Entity payload = Entity.json(" { \"order\": { \"channel_id\": 46019, \"customer_id\": 9460508, \"line_items_attributes\": [ { \"quantity\": 5, \"sellable_id\": 7540777 } ] } }");
Response response = client.target("https://api.veeqo.com/orders")
  .request(MediaType.APPLICATION_JSON_TYPE)
  .header("x-api-key", APIKEY)
  .post(payload);

		resp.getWriter().println("status: " + response.getStatus());
		resp.getWriter().println("headers: " + response.getHeaders());
		resp.getWriter().println("body:" + response.readEntity(String.class));
	}



}
