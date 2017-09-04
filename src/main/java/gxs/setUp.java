package gxs;

import java.io.IOException;
import java.io.ObjectOutputStream;
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

public class setUp extends HttpServlet {

		@Override
		public void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws IOException {
			
			try {
				Cache cache;
				CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
				cache = cacheFactory.createCache(Collections.emptyMap());
				cache.put("logs", "Start of logs \r\n");
				//SET AS WHERE YOU WISH TO START
				cache.put("firstPoSeen", "4517075836");
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
