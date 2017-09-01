package gxs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class currentStatusReport extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			Cache cache;
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			cache = cacheFactory.createCache(Collections.emptyMap());
			ArrayList<StatusReport> listOfReports = (ArrayList<StatusReport>) cache.get("currentStatusReports");
			for(StatusReport s : listOfReports )
			{
				resp.getWriter().print(s.orderNumber + " " + s.status + "\r\n");
			}
		} catch (CacheException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
