package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import baseblocksystem.servletBase;

/**
 * Servlet implementation class StatisticController
 * 
 * A xx page. 
 * 
 * Description of the class.
 * 
 * @author Ferit Bölezek ( Enter name if you've messed around with this file ;) )
 * @version 1.0
 * 
 */

@WebServlet("/StatisticsPage")
public class StatisticController extends servletBase {
	
	
	public StatisticController() {
		super();
	}
	
	
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		out.println(getPageIntro());
		

		String username = req.getParameter("username"); // get the string that the user entered in the form
		String from = req.getParameter("from"); // get the entered password
		String to = req.getParameter("to");
		String activity = req.getParameter("activity");
		String role = req.getParameter("role");
		
		
		System.out.println("username: " + username);
		System.out.println("from: " + from);
		System.out.println("to: " + to);
		System.out.println("activity: " + activity);
		System.out.println("role: " + role);
		
        out.println(statisticsPageForm());
		
    }
		
	private String statisticsPageForm() {
			return "    <link rel=\"stylesheet\" type=\"text/css\" href=\"StyleSheets/StatisticsController.css\">\r\n" + 
	        		"    "
	        		+ "<div class=\"wrapper\">\r\n" + 
	        		"        <div class=\"\">\r\n" + 
	        		"            <form id=\"filter_form\" onsubmit=\"checkInput()\">\r\n" + 
	        		"                <div id=\"stat_title\">\r\n" + 
	        		"                    <p id=\"stat_title_text\">STATISTICS</p>\r\n" + 
	        		"                </div>\r\n" + 
	        		"\r\n" + 
	        		"                <div class=\"filter_row\">\r\n" + 
	        		"                    <div>\r\n" + 
	        		"                    <p class=\"descriptors\">Username</p>\r\n" + 
	        		"                    <input class=\"credentials_rect\" type=\"text\" id=\"username\" name=\"username\" pattern=\"^[a-zA-Z0-9]*$\" title=\"Please enter letters and numbers only.\" maxlength=\"10\" placeholder=\"Search for user\" required><br>\r\n" + 
	        		"                    </div>\r\n" + 
	        		"                    <div>\r\n" + 
	        		"                        <div>\r\n" + 
	        		"                        <p class=\"descriptors\">From</p>\r\n" + 
	        		"                        <p class=\"descriptors\" style=\"margin-left: 140px;\">To</p>\r\n" + 
	        		"                    </div>\r\n" + 
	        		"                    <div id=\"stat_date_picker\">\r\n" + 
	        		"                    <input type=\"date\" id=\"from\" name=\"from\">\r\n" + 
	        		"                    <input type=\"date\" id=\"to\" name=\"to\">\r\n" + 
	        		"                </div>\r\n" + 
	        		"            </div>\r\n" + 
	        		"            <div>\r\n" + 
	        		"                <p class=\"descriptors\">Activity</p>\r\n" + 
	        		"                <div id=\"activity_picker\">\r\n" + 
	        		"                    <select id=\"act_picker\" name=\"activity\" form=\"filter_form\">\r\n" + 
	        		"                        <option value=\"SDP\">SDP</option>\r\n" + 
	        		"                        <option value=\"SRS\">SRS</option>\r\n" + 
	        		"                        <option value=\"SVVS\">SVVS</option>\r\n" + 
	        		"                        <option value=\"STLDD\">STLDD</option>\r\n" + 
	        		"                        <option value=\"SVVI\">SVVI</option>\r\n" + 
	        		"                        <option value=\"SDDD\">SDDD</option>\r\n" + 
	        		"                        <option value=\"SVVR\">SVVR</option>\r\n" + 
	        		"                        <option value=\"SSD\">SSD</option>\r\n" + 
	        		"                        <option value=\"Slutrapport\">Slutrapport</option>\r\n" + 
	        		"                        <option value=\"Funktionstest\">Funktionstest</option>\r\n" + 
	        		"                        <option value=\"Systemtest\">Systemtest</option>\r\n" + 
	        		"                        <option value=\"Regressionstest\">Regressionstest</option>\r\n" + 
	        		"                        <option value=\"Mote\">Möte</option>\r\n" + 
	        		"                        <option value=\"Foreläsning\">Föreläsning</option>\r\n" + 
	        		"                        <option value=\"Ovning\">Övning</option>\r\n" + 
	        		"                        <option value=\"Terminalovning\">Terminalövning</option>\r\n" + 
	        		"                        <option value=\"Sjalvstudier\">Självstudier</option>\r\n" + 
	        		"                        <option value=\"Ovrigt\">Övrigt</option>\r\n" + 
	        		"                      </select>\r\n" + 
	        		"                        \r\n" + 
	        		"                </div>\r\n" + 
	        		"            </div>\r\n" + 
	        		"            <div>\r\n" + 
	        		"                <p class=\"descriptors\">Role</p>\r\n" + 
	        		"                <div id=\"activity_picker\">\r\n" + 
	        		"                    <select id=\"act_picker\" name=\"role\" form=\"filter_form\">\r\n" + 
	        		"                        <option value=\"Projektledare\">Projektledare</option>\r\n" + 
	        		"                        <option value=\"Systemansvarig\">Systemansvarig</option>\r\n" + 
	        		"                        <option value=\"Utvecklare\">Utvecklare</option>\r\n" + 
	        		"                        <option value=\"Testare\">Testare</option>\r\n" + 
	        		"                      </select>\r\n" + 
	        		"                        \r\n" + 
	        		"                </div>\r\n" + 
	        		"            </div>\r\n" + 
	        		"                <input class=\"submitBtn\" type=\"submit\" value=\"Search\">\r\n" + 
	        		"                </div>\r\n" + 
	        		"              </form> \r\n" + 
	        		"        </div>\r\n" + 
	        		"        <div>\r\n" + 
	        		"            <table id=\"stats\">\r\n" + 
	        		"                <tr>\r\n" + 
	        		"                  <th>Total</th>\r\n" + 
	        		"                  <th>V.4</th>\r\n" + 
	        		"                  <th>V.5</th>\r\n" + 
	        		"                  <th>TOT</th>\r\n" + 
	        		"                </tr>\r\n" + 
	        		"                <tr>\r\n" + 
	        		"                  <td>SDP</td>\r\n" + 
	        		"                  <td>4324</td>\r\n" + 
	        		"                  <td>234</td>\r\n" + 
	        		"                </tr>\r\n" + 
	        		"                <tr>\r\n" + 
	        		"                  <td>SRS</td>\r\n" + 
	        		"                  <td>234</td>\r\n" + 
	        		"                  <td>21</td>\r\n" + 
	        		"                </tr>\r\n" + 
	        		"                <tr>\r\n" + 
	        		"                  <td>SVVS</td>\r\n" + 
	        		"                  <td>0</td>\r\n" + 
	        		"                  <td>31</td>\r\n" + 
	        		"                </tr>\r\n" + 
	        		"                <tr>\r\n" + 
	        		"                  <td>SVVI</td>\r\n" + 
	        		"                  <td>32</td>\r\n" + 
	        		"                  <td>70</td>\r\n" + 
	        		"                </tr>\r\n" + 
	        		"                <tr>\r\n" + 
	        		"                  <td>STLDD</td>\r\n" + 
	        		"                  <td>30</td>\r\n" + 
	        		"                  <td>21</td>\r\n" + 
	        		"                </tr>\r\n" + 
	        		"                <tr>\r\n" + 
	        		"                  <td>SDDD</td>\r\n" + 
	        		"                  <td>340</td>\r\n" + 
	        		"                  <td>560</td>\r\n" + 
	        		"                </tr>\r\n" + 
	        		"                <tr>\r\n" + 
	        		"                  <td>SVVR</td>\r\n" + 
	        		"                  <td>340</td>\r\n" + 
	        		"                  <td>560</td>\r\n" + 
	        		"                </tr>\r\n" + 
	        		"                <tr>\r\n" + 
	        		"                  <td>SSD</td>\r\n" + 
	        		"                  <td>32</td>\r\n" + 
	        		"                  <td>32</td>\r\n" + 
	        		"                </tr>\r\n" + 
	        		"                <tr>\r\n" + 
	        		"                  <td>PFR</td>\r\n" + 
	        		"                  <td>4324</td>\r\n" + 
	        		"                  <td>41</td>\r\n" + 
	        		"                </tr>\r\n" + 
	        		"                <tr>\r\n" + 
	        		"                  <td>Möte(p-grupp)</td>\r\n" + 
	        		"                  <td>40</td>\r\n" + 
	        		"                  <td>90</td>\r\n" + 
	        		"                </tr>\r\n" + 
	        		"                <tr>\r\n" + 
	        		"                    <td>Möte(s-chef)</td>\r\n" + 
	        		"                    <td>8654</td>\r\n" + 
	        		"                    <td>421</td>\r\n" + 
	        		"                  </tr>\r\n" + 
	        		"                  <tr>\r\n" + 
	        		"                    <td>Funktionstest</td>\r\n" + 
	        		"                    <td>412</td>\r\n" + 
	        		"                    <td>412</td>\r\n" + 
	        		"                  </tr>\r\n" + 
	        		"                  <tr>\r\n" + 
	        		"                    <td>Systemtest</td>\r\n" + 
	        		"                    <td>32</td>\r\n" + 
	        		"                    <td>83</td>\r\n" + 
	        		"                  </tr>\r\n" + 
	        		"                  <tr>\r\n" + 
	        		"                    <td>Regressionstest</td>\r\n" + 
	        		"                    <td>30</td>\r\n" + 
	        		"                    <td>70</td>\r\n" + 
	        		"                  </tr>\r\n" + 
	        		"                  <tr>\r\n" + 
	        		"                    <td><b>Total<b></td>\r\n" + 
	        		"                    <td>9313</td>\r\n" + 
	        		"                    <td>6234</td>\r\n" + 
	        		"                  </tr>\r\n" + 
	        		"              </table>\r\n" + 
	        		"              \r\n" + 
	        		"        </div>\r\n" + 
	        		"    </div>";
	}
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	doGet(req, resp);
    }
	
	
}
