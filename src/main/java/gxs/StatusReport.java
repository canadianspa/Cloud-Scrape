package gxs;

import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class StatusReport {

	String orderNumber,transactionDate,originalCustomerOrderNumber,status;
	String APIKEY = "***REMOVED***";
	ArrayList<String> quanity,sku,price,tax;
	Customer customer;
	String veeqoOrderNumber;

	
	
	public StatusReport(String orderNumber, String transactionDate, String originalCustomerOrderNumber, String status,
			ArrayList<String> quanity, ArrayList<String> article, ArrayList<String> price, ArrayList<String> tax,
			Customer customer) throws Exception {
		super();
		this.orderNumber = orderNumber;
		this.transactionDate = transactionDate;
		this.originalCustomerOrderNumber = originalCustomerOrderNumber;
		this.status = status;
		this.quanity = quanity;
		//converts each article to sku
		for(String a :article)
		{
			sku.add(convertToSku(a));
		}
		this.price = price;
		this.tax = tax;
		this.customer = customer;
	}
	public StatusReport(String orderNumber, String transactionDate, String originalCustomerOrderNumber,
			ArrayList<String> quanity, ArrayList<String> sku, ArrayList<String> price, ArrayList<String> tax,
			Customer customer) {
		super();
		this.orderNumber = orderNumber;
		this.transactionDate = transactionDate;
		this.originalCustomerOrderNumber = originalCustomerOrderNumber;
		this.quanity = quanity;
		this.sku = sku;
		this.price = price;
		this.tax = tax;
		this.customer = customer;
	}
	//finds article code in ToSku and returns the sku
	public String convertToSku(String article) throws Exception
	{
		String sku = "";
		String lines[] = sharedInformation.skuCsv.split("\\r?\\n");
		for(String s : lines)
		{
			String line[] = s.split(",");
			if(line[1].equals(article))
			{
				sku = line[0];
				break;
			}
		
		}
		if(sku.equals(""))
		{
			throw new Exception("sku/article not found");
		}
		return sku;
	}

	public String convertToSellableID(String sku)
	{

		Client client = ClientBuilder.newClient();
		Response response = client.target("https://api.veeqo.com/products?query=" + sku)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("x-api-key", APIKEY)
				.get();
		String body = response.readEntity(String.class);

		String sellableID = body.substring(body.indexOf("sellable_id") + 13,body.indexOf(",",body.indexOf(("sellable_id"))));
		System.out.println("sellable id " + sellableID);
		return sellableID;

	}
	
	public void delivered()
	{
		status = "C50";
	}
	public void uploadOrder()
	{
		Client client = ClientBuilder.newClient();
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
		Entity payload = Entity.json("\r\n" + 
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
		Response response = client.target("https://api.veeqo.com/orders")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("x-api-key", APIKEY)
				.post(payload);

		//if response is error add to the logs
		//add veeqo order number 
		String sResponse = response.readEntity(String.class);
		
		if (sResponse.equals("{\r\n" + 
				"  \"status\": \"404\",\r\n" + 
				"  \"error\": \"Not Found\"\r\n" + 
				"}"))
		{
			sharedInformation.logs += "problem with order with name: " + customer.firstName + "\r\n";
		}
		else
		{
			String id = sResponse.split("\"id\"")[2];
			id = id.substring(2, id.indexOf("\r\n")-1);
			veeqoOrderNumber = id;
		}
	
	}
	
	

	

}
