package servlets;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import entities.BNQPurchaseOrder;
import entities.HomeBasePurchaseOrder;
import entities.ListOfLogs;
import entities.Logs;
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
				HomeBasePurchaseOrder p = new HomeBasePurchaseOrder("4517082080");
				ObjectifyService.ofy().save().entities(p);
				
				//SET AS WHERE YOU WISH TO START
				BNQPurchaseOrder b = new BNQPurchaseOrder("84043526");
				ObjectifyService.ofy().save().entities(b);
			} catch (Exception e) {
				resp.getWriter().write(e.getMessage());
			}

		}
}
