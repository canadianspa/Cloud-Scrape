package gxs;

import java.util.ArrayList;
import java.util.Collections;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class StatusReport implements Serializable {

	@Parent Key<ListOfReports> theList;
	@Id public String orderNumber;
	public String transactionDate,originalCustomerOrderNumber,status;
	public ArrayList<String> quanity,sku,price,tax;
	public Customer customer;
	public String veeqoOrderNumber;

	public StatusReport(String orderNumber, String transactionDate, String originalCustomerOrderNumber,
			ArrayList<String> article, ArrayList<String> quanity, ArrayList<String> price, ArrayList<String> tax,
			Customer customer) throws Exception {
		super();

		theList = Key.create(ListOfReports.class, "default");	   
		this.orderNumber = orderNumber;
		this.transactionDate = transactionDate;
		this.originalCustomerOrderNumber = originalCustomerOrderNumber;
		this.status = "C30";
		this.quanity = quanity;
		//converts each article to sku
		sku = new ArrayList<String>();
		for(String a :article)
		{
			sku.add(sharedInformation.convertToSku(a));
		}
		this.price = price;
		this.tax = tax;
		this.customer = customer;
		veeqoOrderNumber = "not set yet";

	}
    private StatusReport() {}

	public String  convertToSellableID(String sku)
	{
		try {

			URL url = new URL("https://api.veeqo.com/products?query=" + sku);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("x-api-key", sharedInformation.APIKEY);

			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			String body = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);
			body = body.substring(body.indexOf("\"sku_code\":\"" + sku + "\""));
			String sellableID = body.substring(body.indexOf("sellable_id") + 13,body.indexOf(",",body.indexOf(("sellable_id"))));
			System.out.println("sellable id " + sellableID);
			return sellableID;


		} catch (Exception e) {

			return e.getMessage();
		}
	}

	public void delivered()
	{
		status = "C50";
	}

	public void uploadOrder() throws Exception
	{
		//try {
			Cache cache;
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			cache = cacheFactory.createCache(Collections.emptyMap());
			String lineItemAttributes = "";
			for(int i =0 ; i < sku.size() -1; i ++)
			{

				 lineItemAttributes +=  "            {\r\n" +  
				          "                \"quantity\": "+quanity.get(i)+",\r\n" +  
				          "                \"sellable_id\": "+convertToSellableID(sku.get(i))+",\r\n" +  
				          "                \"price_per_unit\": "+price.get(i)+",\r\n" +  
				          "                \"tax_rate\": "+tax.get(i)+"\r\n" +  
				          "            },\r\n"; 
				    } 
				    lineItemAttributes +=  "            {\r\n" +  
				        "                \"quantity\": "+quanity.get(quanity.size() -1)+",\r\n" +  
				        "                \"sellable_id\": "+convertToSellableID(sku.get(quanity.size() -1))+",\r\n" +  
				        "                \"price_per_unit\": "+price.get(quanity.size() -1)+",\r\n" +  
				        "                \"tax_rate\": "+tax.get(quanity.size() -1)+"\r\n" +  
				        "            }\r\n"; 
				    String payload = ("\r\n" +  
				        "{\r\n" +  
				        "    \"order\": {\r\n" +  
				        "        \"channel_id\": 46687,\r\n" +  
				        "        \"customer_id\": 7014549,\r\n" +  
				        "        \"deliver_to_attributes\": {\r\n" +  
				        "            \"address1\": \""+customer.addr1+"\",\r\n" +  
				        "            \"address2\": \""+customer.addr2+"\",\r\n" +  
				        "            \"city\": \""+customer.city+"\",\r\n" +  
				        "            \"company\": \""+customer.company+"\",\r\n" +  
				        "            \"country\": \""+customer.country+"\",\r\n" +  
				        "            \"customer_id\": 7155742,\r\n" +  
				        "            \"first_name\": \""+customer.firstName+"\",\r\n" +  
				        "            \"last_name\": \""+customer.lastName+"\",\r\n" +  
				        "            \"phone\": \""+customer.phone+"\",\r\n" +  
				        "            \"state\": \"\",\r\n" +  
				        "            \"zip\": \""+customer.zip+"\"\r\n" +  
				        "        },\r\n" +  
				        "        \"line_items_attributes\": [\r\n" +  
				        lineItemAttributes + 
				        "        ]\r\n" +  
				        "    }\r\n" +  
				        "}"); 

			URL url = new URL("https://api.veeqo.com/orders");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("x-api-key", sharedInformation.APIKEY);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(payload);
			writer.close();
			String sResponse = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);
			if (sResponse.equals("{\r\n" + 
					"  \"status\": \"404\",\r\n" + 
					"  \"error\": \"Not Found\"\r\n" + 
					"}"))
			{
				cache.put("logs", cache.get("logs") + "problem with order with name: " + customer.firstName + "\r\n");
			}
			else
			{
				String id =  sResponse.substring(sResponse.indexOf("id") + 4,sResponse.indexOf(",",sResponse.indexOf(("id"))));
				veeqoOrderNumber = id;
				ObjectifyService.ofy().save().entity(this);
				cache.put("logs", cache.get("logs") + "order uploaded with id: " + veeqoOrderNumber + "\r\n");
			}


		//} catch (Exception e) {

			//e.printStackTrace();
		//}

	}






}
