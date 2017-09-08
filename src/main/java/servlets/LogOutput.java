package servlets;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import entities.ListOfLogs;
import entities.Logs;

//output what is in log
//should try and make new lines work
public class LogOutput extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Key<ListOfLogs> theLogs = Key.create(ListOfLogs.class, "default");

		List<Logs> logs = ObjectifyService.ofy()
				.load()
				.type(Logs.class)
				.ancestor(theLogs)
				.list();// Anyone in this book
		
		Collections.sort(logs, new Comparator<Logs>() {
			  public int compare(Logs o1, Logs o2) {
			      return o1.time.compareTo(o2.time);
			  }
			});
		for(Logs l : logs)
		{
			resp.getWriter().println(l.message);
		}
	}

}
