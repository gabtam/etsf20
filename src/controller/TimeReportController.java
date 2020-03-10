package controller;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import baseblocksystem.servletBase;
import database.ActivityReport;
import database.ActivitySubType;
import database.ActivityType;
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
 * @author Ferit B�lezek ( Enter name if you've messed around with this file ;)
 *         )
 * @version 1.0
 * 
 */

// wtf is this i try to fix but it never work

@WebServlet("/TimeReportPage")
public class TimeReportController extends servletBase {

	DatabaseService dbService;

	public TimeReportController() {
		super();
		try {
			this.dbService = new DatabaseService();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 
	 * private ActivityReport createActivityReport(int activityTypeId, int
	 * activitySubTypeId, LocalDate date, int minutes, int userId, int projectId ) {
	 * 
	 * 
	 * finnstidrapport annars skapa tidrapport skapa aktivitetsrapport i
	 * vilketfallsom minutes 1440 annars exceptions
	 * 
	 * return null; }
	 */

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		try {
			out.println(getCurrentUserTimereports());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String activityType = req.getParameter("activity"); // get activity
		String subType = req.getParameter("subType"); // get activity subtype
		String timeSpent = req.getParameter("timeSpent"); // get timespent (integer.parseInt()?)
		
		String timeReportId = req.getParameter("timeReportId");
		System.out.println(timeReportId);
		// System.out.println(activityType);
		// System.out.println(subType);

		// System.out.println(timeSpent);
		
		if(!timeReportId.equals(0)) {
			int id = Integer.parseInt(timeReportId);
			try {
				out.println(getActivityReports(id));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private String getActivityReports(int timeReportId) throws Exception {

		boolean isProjectLeader = false;
		
		if(this.isProjectLeader(null, timeReportId)) { //TODO: Vad ska vi ha för inparametrar
			 isProjectLeader = true;
		}
		
		boolean reportIsSigned = dbService.getTimeReportById(timeReportId).isFinished(); //TODO: Is finished eller is signed??
		
		String html = "<table width=\"600\" border=\"2\">\r\n" + "<tr>\r\n" + "<td> Datum </td>\r\n"
				+ "<td> Aktivitetstyp </td>\r\n" + "<td> Subtyp </td>\r\n" + "<td> Minuter </td>\r\n"
				+ "<td> Ta bort aktivitetsrapport </td>\r\n";

		List<ActivityReport> activityReports = dbService.getActivityReports(timeReportId);
		List<ActivityType> activityTypes = dbService.getActivityTypes();
		List<ActivitySubType> activitySubTypes = dbService.getActivitySubTypes();

		String activityType;
		String activitySubType;

		for (ActivityReport aReport : activityReports) {
			
			activityType = getActivityType(aReport, activityTypes);
			activitySubType = getActivitySubType(aReport, activitySubTypes);
			

			html +=   "<tr>\r\n" 
					+ "<td>" + aReport.getReportDate().toString() + "</td>\r\n"
					+ "<td>" + activityType + "</td>\r\n" 
					+ "<td>" + activitySubType + "</td>\r\n" 
					+ "<td>" + aReport.getMinutes() + "</td>\r\n";  //TODO: ska gå att ändra i tabellen om rapporten inte är signerad
			
			if(!reportIsSigned) { //If timereport isn't signed, show button for deleting timeReport, else dont show it.
				html += "<td> <form method=\"get\"> <button name=\"activityReportId\" type=\"submit\" value=\"" + aReport.getActivityReportId() + "\"> Ta bort </button>  </form> \r\n";	//TODO: ska gå att tabort aktivitetsrapport om den inte är signerad
				
			}
			
			
			if(isProjectLeader)	{
				
				if(reportIsSigned) { //TODO: Frågan i discord 
					html += "<td> <form method=\"get\"> <button name=\"timeReportIdToUnsign\" type=\"submit\" value=\"" + aReport.getTimeReportId() 
					+ "\"> Avsignera </button>  </form> \r\n" ;
				}
				
				else {
					html += "<td> <form method=\"get\"> <button name=\"timeReportIdToSign\" type=\"submit\" value=\"" + aReport.getTimeReportId() 
					+ "\"> Signera </button>  </form> \r\n";
					
					
				}
			}
		}		

		html += "</tr>\r\n" + "</table>"; // END HTML

		return html;
	}
	
	

	private String getActivityType(ActivityReport activityReport, List<ActivityType> typeList) throws SQLException {
		
		   for(ActivityType aType: typeList) { //Get activity type for current activity report
			   
			   if(aType.getActivityTypeId() == activityReport.getActivityTypeId()) {
				   return aType.getType();
			   }
		   }
		   
		   return ""; //TODO: EXCEPTION?? Borde alltid hitta en type.
			
		}
	
	private String getActivitySubType(ActivityReport activityReport, List<ActivitySubType> subTypeList) throws SQLException{
		
		for (ActivitySubType aSubType : subTypeList) { // Get activity type for current activity report
				
			if (aSubType.getActivitySubTypeId() == activityReport.getActivitySubTypeId()) { 
				
				return aSubType.getSubType();
			}
		}
		
		return ""; //TODO: EXCEPTION?? Borde alltid hitta en subtype.
	}

	private String getUserTimereports(User user) throws SQLException {
		String html = "<table width=\"400\" border=\"2\">\r\n" 
				+ "<tr>\r\n" 
				+ "<td> week </td>\r\n"
				+ "<td> timespent(minutes) </td>\r\n" 
				+ "<td> Status </td>\r\n" 
				+ "<td> Välj tidrapport </td>\r\n"
				+ "<td> Ta bort tidrapport </td>\r\n"; //TODO: Ska ej gå att ta bort om den är signerad

		List<TimeReport> userTimeReports = dbService.getTimeReportsByUser(user.getUserId()); //TODO:  Get all timereports for logged in user

		for (TimeReport tr : userTimeReports) {

			int timeReportTotalTime = getTotalTimeReportTime(tr);
			String signed;

			if (tr.isSigned()) { // get isSigned or not
				signed = "Signerad";
			} else {
				signed = "Ej signerad";
			}

			html += "<tr>\r\n" + "<td>" + tr.getWeek() + "</td>\r\n" + // set values into HTML
					"<td>" + timeReportTotalTime + "</td>\r\n" + "<td>" + signed + "</td>\r\n"
					+ "<td> <form method=\"get\"> <button name=\"timeReportId\" type=\"submit\" value=\"" + tr.getTimeReportId() 
					+ "\"> Välj </button>  </form> \r\n" 
					+ "<td> <input class=\"submitBtn\" type=\"submit\" value=\"Ta bort\">\r\n" //TODO: Koppla till att ta bort tidrapport
					+ "</td>\r\n" + "</tr>\r\n";

		}

		html += "</tr>\r\n" + "</table>"; // END HTML

		return html;

	}
	/**
	 * Project leader retrives a table of all unsigned timereports.
	 * @return HTML Page
	 * @throws SQLException
	 */
	private String getUnsignedTimeReports() throws SQLException { 
		
		if(!this.isProjectLeader()) {
			//TODO: Returnera någon exception
		}
		
		String html;
		
		List <TimeReport> allTimeReports = dbService.getTimeReportsByProject(1); //TODO: Hur får man projektID
		List <TimeReport> unsignedTimeReports = new ArrayList<TimeReport>();
		
		for(TimeReport tr : allTimeReports) {
			
			if(!tr.isSigned()) {
				unsignedTimeReports.add(tr);
			}
		}
		
		
		 html = "<table width=\"400\" border=\"2\">\r\n" 
				+ "<tr>\r\n" 
				+ "<td> week </td>\r\n"
				+ "<td> timespent(minutes) </td>\r\n" 
				+ "<td> Status </td>\r\n" 
				+ "<td> Välj tidrapport </td>\r\n"
				+ "<td> Ta bort tidrapport </td>\r\n"; //TODO: Ska ej gå att ta bort om den är signerad

		

		for (TimeReport tr : unsignedTimeReports) {

			int timeReportTotalTime = getTotalTimeReportTime(tr);
			String signed;

			if (tr.isSigned()) { // get isSigned or not
				signed = "Signerad";
			} else {
				signed = "Ej signerad";
			}
			
			User trOwner = dbService.getUserById(tr.getProjectUserId());

			html += "<tr>\r\n" + "<td>" + tr.getWeek() + "</td>\r\n" + // set values into HTML
					"<td>" + trOwner.getUsername() + "</td>\r\n" +
					"<td>" + timeReportTotalTime + "</td>\r\n"
					+ "<td>" + signed + "</td>\r\n" //Should be "Ej signerad" for all reports
					+ "<td> <form method=\"get\"> <button name=\"timeReportId\" type=\"submit\" value=\"" + tr.getTimeReportId() + "\"> Välj </button> </form> \r\n"
					+ "<td> <input class=\"submitBtn\" type=\"submit\" value=\"Ta bort\">\r\n" //TODO: Koppla till att ta bort tidrapport
					+ "</td>\r\n" + "</tr>\r\n";

		}

		html += "</tr>\r\n" + "</table>"; // END HTML

		return html;		
	}

	private int getTotalTimeReportTime(TimeReport tr) throws SQLException {

		int totalTime = 0;

		List<ActivityReport> activitiesInTimeReport = dbService.getActivityReports(tr.getTimeReportId());

		for (ActivityReport ar : activitiesInTimeReport) { // calculate total time from all activity reports inside this
															// timeReport
			totalTime += ar.getMinutes();
		}
		return totalTime;
	}

	private String activityReportForm() {
		return "<link rel=\"stylesheet\" type=\"text/css\" href=\"StyleSheets/SessionController.css\">"
				+ " <form id=\"filter_form\" onsubmit=\"checkInput()\">\r\n" + "                 Aktivitetstyp\r\n"
				+ "                <div id=\"activity_picker\">\r\n"
				+ "                    <select id=\"act_picker\" name=\"activity\" form=\"filter_form\">\r\n"
				+ "                        <option value=\"SDP\">SDP</option>\r\n"
				+ "                        <option value=\"SRS\">SRS</option>\r\n"
				+ "                        <option value=\"SVVS\">SVVS</option>\r\n"
				+ "                        <option value=\"STLDD\">STLDD</option>\r\n"
				+ "                        <option value=\"SVVI\">SVVI</option>\r\n"
				+ "                        <option value=\"SDDD\">SDDD</option>\r\n"
				+ "                        <option value=\"SVVR\">SVVR</option>\r\n"
				+ "                        <option value=\"SSD\">SSD</option>\r\n"
				+ "                        <option value=\"Slutrapport\">Slutrapport</option>\r\n"
				+ "                        <option value=\"Funktionstest\">Funktionstest</option>\r\n"
				+ "                        <option value=\"Systemtest\">Systemtest</option>\r\n"
				+ "                        <option value=\"Regressionstest\">Regressionstest</option>\r\n"
				+ "                        <option value=\"Mote\">Möte</option>\r\n"
				+ "                        <option value=\"Foreläsning\">Föreläsning</option>\r\n"
				+ "                        <option value=\"Ovning\">Övning</option>\r\n"
				+ "                        <option value=\"Terminalovning\">Terminalövning</option>\r\n"
				+ "                        <option value=\"Sjalvstudier\">Självstudier</option>\r\n"
				+ "                        <option value=\"Ovrigt\">Övrigt</option>\r\n"
				+ "                      </select>\r\n" + "                </div>\r\n" + "            </div>\r\n"
				+ "            <div>\r\n" + "                <p class=\"descriptors\">Aktivitet subtyp</p>\r\n"
				+ "                <div id=\"activity_picker\">\r\n"
				+ "                    <select id=\"act_picker\" name=\"subType\" form=\"filter_form\">\r\n"
				+ "					     <option value=\"\"></option>\r\n"
				+ "                        <option value=\"Utveckling\">Utveckling</option>\r\n"
				+ "                        <option value=\"Omarbete\">Omarbete</option>\r\n"
				+ "                        <option value=\"Informellgranskning\">Informellgranskning</option>\r\n"
				+ "                        <option value=\"Formellgranskning\">Formellgranskning</option>\r\n"
				+ "                      </select>\r\n" + "                </div>\r\n" + "            </div>\r\n"
				+ "                <p class=\"descriptors\">Tid spenderad (i minuter) </p>\r\n"
				+ "                <div id=\"activity_picker\">\r\n" + "				</div>"
				+ "              <input class=\"credentials_rect\" type=\"text\" id=\"timeSpent\" name=\"timeSpent\" pattern=\"^[0-9]*$\" title=\"Please enter numbers only.\" maxlength=\"4\" placeholder=\"Tid Spenderad\" required><br>\r\n"
				+ "              <input class=\"submitBtn\" type=\"submit\" value=\"Skicka in\">\r\n"
				+ "                </div>\r\n" + "              </form> ";

		// html += activitysubtype.getId...
	}

}