package entities;

import java.util.Date;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class Logs {
	
	@Parent Key<ListOfLogs> theList;
	public String message;
	public Date time;
	@Id Long id;
	public Logs(String message) {
		super();
		theList = Key.create(ListOfLogs.class, "default");	
		this.message = message;
		this.time = new Date();
	}
	public Logs(){}
	
	
	
	
}
