package gxs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

public class currentStatusReport extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		Key<ListOfReports> theBook = Key.create(ListOfReports.class, "default");

		List<StatusReport> reports = ObjectifyService.ofy()
				.load()
				.type(StatusReport.class) // We want only Greetings
				.ancestor(theBook)
				.list();// Anyone in this book
		for(StatusReport s : reports )
		{
			resp.getWriter().print(s.orderNumber + " " + s.status + "\r\n");
		}

	}

}
