package controller;

import java.sql.Date;  //oklart evt util
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import baseblocksystem.servletBase;
import database.ActivityReport;
import database.DatabaseService;
import database.TimeReport;
import database.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TimeReportController
 * 
 * A xx page. 
 * 
 * Description of the class.
 * 
 * @author Ferit B�lezek ( Enter name if you've messed around with this file ;) )
 * @version 1.0
 * 
 */


// wtf is this i try to fix but it never work

@WebServlet("/TimeReportPage")
public class TimeReportController extends servletBase {
	
	DatabaseService dbService;

	public TimeReportController( ) {
		super();
		try {
			DatabaseService dbService = new DatabaseService();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
/*private ActivityReport createActivityReport(int activityTypeId, int activitySubTypeId, Date date, int minutes, int userId, int projectId ) {
	
	
	return null;
 }
*/
	
	   @Override
	    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			PrintWriter out = resp.getWriter();
			out.println(getPageIntro());
			

			String activityType = req.getParameter("activity"); // get activity
			String subType = req.getParameter("subType"); // get activity subtype
			String timeSpent = req.getParameter("timeSpent"); //get timespent (integer.parseInt()?)
			
		
			
		//	System.out.println(activityType);
		//	System.out.println(subType);

		//	System.out.println(timeSpent);
	        
	        
		
			try {
				out.println(getTableAllTimereports());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
	    }
	   
	   private String getTableAllTimereports() throws SQLException {
			String html ="<table width=\"400\" border=\"2\">\r\n";
	   
						User currentUser = new User(1, "a", "a", false);//getLoggedInUser(req); //ÄR DETTA RÄTT?
						System.out.println(dbService);
						List<TimeReport> userTimeReports = dbService.getTimeReportsByUser(1); //Get all timereports for logged in user
					
					for(TimeReport tr : userTimeReports) {
						
						int timeReportTotalTime = getTotalTimeReportTime(tr);
						String signed;
						
						
						
						if(tr.isSigned()) {  											//get isSigned or not
							signed = "Signerad";
						}
						else {
							signed = "Ej signerad";
						}
						
						
						html += "<tr>\r\n" + 
					"<td>" + tr.getWeek() + "</td>\r\n" +  							//set values into HTML
					"<td>" + timeReportTotalTime +"d</td>\r\n" +
					"<td>" + signed + "</td>\r\n" +
					"<td> <input class=\"submitBtn\" type=\"submit\" value=\"Välj tidrapport\">\r\n" + 
					"<td> <input class=\"submitBtn\" type=\"submit\" value=\"Tabort\">\r\n" + 			//TODO: remove knapp
					"</td>\r\n" +  
					"</tr>\r\n";
					}
					
					
					
					
					 html += "</tr>\r\n" + 
					"</table>"; 			//END HTML
					
					
					return html;
					
					
	}
			
	   private int getTotalTimeReportTime(TimeReport tr) throws SQLException {
		   
		   int totalTime = 0;
		   
		   List<ActivityReport> activitiesInTimeReport = dbService.getActivityReports(tr.getTimeReportId());
			
			for(ActivityReport ar : activitiesInTimeReport) { //calculate total time from all activity reports inside this timeReport
				totalTime += ar.getMinutes();
			}
		   
			return totalTime;
	   }
	   
		private String activityReportForm() {
				return 	"<link rel=\"stylesheet\" type=\"text/css\" href=\"StyleSheets/SessionController.css\">"+
						" <form id=\"filter_form\" onsubmit=\"checkInput()\">\r\n" + 
						"                 Aktivitetstyp\r\n" + 
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
						"                </div>\r\n" + 
						"            </div>\r\n" + 
						"            <div>\r\n" + 
						"                <p class=\"descriptors\">Aktivitet subtyp</p>\r\n" + 
						"                <div id=\"activity_picker\">\r\n" + 
						"                    <select id=\"act_picker\" name=\"subType\" form=\"filter_form\">\r\n" + 
						"					     <option value=\"\"></option>\r\n" + 
						"                        <option value=\"Utveckling\">Utveckling</option>\r\n" + 
						"                        <option value=\"Omarbete\">Omarbete</option>\r\n" + 
						"                        <option value=\"Informellgranskning\">Informellgranskning</option>\r\n" + 
						"                        <option value=\"Formellgranskning\">Formellgranskning</option>\r\n" + 
						"                      </select>\r\n" + 
						"                </div>\r\n" + 
						"            </div>\r\n" + 
						"                <p class=\"descriptors\">Tid spenderad (i minuter) </p>\r\n" + 
						"                <div id=\"activity_picker\">\r\n"+ 
						"				</div>" + 
						"              <input class=\"credentials_rect\" type=\"text\" id=\"timeSpent\" name=\"timeSpent\" pattern=\"^[0-9]*$\" title=\"Please enter numbers only.\" maxlength=\"4\" placeholder=\"Tid Spenderad\" required><br>\r\n" + 
						"              <input class=\"submitBtn\" type=\"submit\" value=\"Skicka in\">\r\n" + 
						"                </div>\r\n" + 
						"              </form> ";
	}

}