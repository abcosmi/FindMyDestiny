package find_my_destiny;

import find_my_destiny.FindMyDestinySearch;
import find_my_destiny.GoogleNearbyApi;
import java.io.IOException;

import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name= "FindPlaceServlet", urlPatterns = {"/destination_search"})
public class FindPlaceServlet extends HttpServlet 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException
	{
		response.setContentType("text/html");
		System.out.println("oooooohoooooo");
		try(PrintWriter out = response.getWriter())
		{
			out.write("<p>eae men kkk</p>");
		}
	}
}
