package gxs;

import java.util.ArrayList;

public class StatusReport {

	String orderNumber,transactionDate,originalCustomerOrderNumber,status;
	
	ArrayList<String> quanity,sellableID;
	Customer customer;

	public StatusReport(String orderNumber, String transactionDate, String originalCustomerOrderNumber,
			ArrayList<String> quanity, ArrayList<String> sellableID, Customer customer) {
		super();
		this.orderNumber = orderNumber;
		this.transactionDate = transactionDate;
		this.originalCustomerOrderNumber = originalCustomerOrderNumber;
		this.status = "C30";
		this.quanity = quanity;
		this.sellableID = sellableID;
		this.customer = customer;
	} 

	public StatusReport(String orderNumber, String transactionDate, String originalCustomerOrderNumber, String status,
			ArrayList<String> quanity, ArrayList<String> sellableID, Customer customer) {
		super();
		this.orderNumber = orderNumber;
		this.transactionDate = transactionDate;
		this.originalCustomerOrderNumber = originalCustomerOrderNumber;
		this.status = status;
		this.quanity = quanity;
		this.sellableID = sellableID;
		this.customer = customer;
	} 
	
	

	

}
