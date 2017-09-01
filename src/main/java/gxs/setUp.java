package gxs;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class setUp extends HttpServlet {

		@Override
		public void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws IOException {
			
			try {
				Cache cache;
				CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
				cache = cacheFactory.createCache(Collections.emptyMap());
				cache.put("logs", "Start of logs \r\n");
				cache.put("firstPoSeen", "4517065956");
				cache.put("currentStatusReports", new ArrayList<StatusReport>());
				
			} catch (Exception e) {
				resp.getWriter().write(e.getMessage());
			}

		}
}
