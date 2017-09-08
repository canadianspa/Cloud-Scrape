package servlets;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import entities.ListOfLogs;
import entities.ListOfReports;
import entities.Logs;
import entities.PurchaseOrder;
import entities.StatusReport;

public class setUp extends HttpServlet {

		@Override
		public void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws IOException {
			
			try {
				//reset the logs
				Key<ListOfLogs> theLogs = Key.create(ListOfLogs.class, "default");

				List<Logs> logs = ObjectifyService.ofy()
						.load()
						.type(Logs.class)
						.ancestor(theLogs)
						.list();// Anyone in this book
				for(Logs l : logs )
				{
					ObjectifyService.ofy().delete().entity(l);
				}
				//SET AS WHERE YOU WISH TO START
				PurchaseOrder p = new PurchaseOrder("4517076599");
				ObjectifyService.ofy().save().entity(p);
				//REMOVES ALL REPORTS
				Key<ListOfReports> theBook = Key.create(ListOfReports.class, "default");

				List<StatusReport> reports = ObjectifyService.ofy()
						.load()
						.type(StatusReport.class)
						.ancestor(theBook)
						.list();// Anyone in this book
				for(StatusReport s : reports )
				{
					ObjectifyService.ofy().delete().entity(s);
				}
				
			} catch (Exception e) {
				resp.getWriter().write(e.getMessage());
			}

		}
}
