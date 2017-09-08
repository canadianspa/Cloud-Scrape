package entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

//holds the first po that was seen last scrape
@Entity
public class PurchaseOrder {
	
	public String poNumber;
	//so it doesnt create multiple ones
	@Id public String oneOf = "default";

	public PurchaseOrder(String poNumber) {
		super();
		this.poNumber = poNumber;
	}
	public PurchaseOrder(){}
	

}
