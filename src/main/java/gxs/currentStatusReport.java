package gxs;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class currentStatusReport extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		
		for(StatusReport s : sharedInformation.currentStatusReports)
		{
			resp.getWriter().write(s.orderNumber + " " + s.status + "\r\n");
		}

	}

}
