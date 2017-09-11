package servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class startHomeBaseScrape extends HttpServlet{
	
	//starts the scraping in a push queue, this was important as without it was timing out after 2.5 pages
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		Queue queue = QueueFactory.getDefaultQueue();
	    queue.add(TaskOptions.Builder.withUrl("/hscrape"));
		resp.getWriter().write("Starting home base Scrape");


	}
}
