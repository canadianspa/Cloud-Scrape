package entities;

import java.util.ArrayList;

public class EDA {
	public String seqNo,storeCode,purchOrderNo,custTellNo1,bqSuppNo,custName;
	public String delDate,dateOrderPlaced;
	public String custAdd1,custAdd2,custAdd3,custAdd4,custPostCode;
	public ArrayList<String> eanCode1 = new ArrayList<String>();
	public ArrayList<String> desc1 = new ArrayList<String>();
	public ArrayList<String> qty1 = new ArrayList<String>();
	public String salesNumber;
	public String homestore = "HOME";
	public String poVerNo = "00001";

	//no del date on the site
	public EDA(String seqNo, String storeCode, String purchOrderNo, String custTellNo1, String bqSuppNo,
			String custName, ArrayList<String> eanCode1, ArrayList<String> desc1, ArrayList<String> qty1,
			String dateOrderPlaced, String delDate,String custAdd1,String custAdd2,String custAdd3,String custAdd4,String custPostCode,String salesNumber) {
		super();
		this.seqNo = seqNo;
		this.storeCode = storeCode;
		this.purchOrderNo = purchOrderNo;
		this.custTellNo1 = custTellNo1;
		this.bqSuppNo = bqSuppNo;
		this.custName = custName;
		this.eanCode1 = eanCode1;
		this.desc1 = desc1;
		this.qty1 = qty1;
		this.dateOrderPlaced = dateOrderPlaced;
		this.delDate = delDate;
		this.custAdd1 = custAdd1;
		this.custAdd2 = custAdd2;
		this.custAdd3 = custAdd3;
		this.custAdd4 = custAdd4;
		this.custPostCode = custPostCode;
		this.salesNumber = salesNumber;

	}
	





}
