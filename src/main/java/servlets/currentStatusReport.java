package servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import entities.ListOfReports;
import entities.StatusReport;

public class currentStatusReport extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		Key<ListOfReports> theBook = Key.create(ListOfReports.class, "default");

		List<StatusReport> reports = ObjectifyService.ofy()
				.load()
				.type(StatusReport.class) 
				.ancestor(theBook)
				.list();// Anyone in this book
		for(StatusReport s : reports )
		{
			resp.getWriter().print(s.orderNumber + " " + s.status +  " " + s.veeqoOrderNumber + "\r\n");
		}

	}

}
