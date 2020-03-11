package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import baseblocksystem.servletBase;
import database.DatabaseService;
import database.Project;
import database.User;

/**
 * Servlet implementation class ProjectController
 * 
 * A xx page. 
 * 
 * Description of the class.
 * 
 * @author Ferit B�lezek ( Enter name if you've messed around with this file ;) )
 * @version 1.0
 * 
 */

@WebServlet("/ProjectPage")
public class ProjectController extends servletBase {
	
	private DatabaseService dbService; // Temporary, will be replaced later.
	
	
	public ProjectController() {
		super();
		try {
			dbService = new DatabaseService();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		out.println(getHeader());
		
		out.println("<body>" + "<link rel=\"stylesheet\" type=\"text/css\" href=\"StyleSheets/ProjectController.css\">\n");
		
		try {
			
		List<Project> plist = dbService.getAllProjects(getLoggedInUser(req).getUserId());
		
		out.println("<h2>Projects</h2>\n" +
        "<table id=\"table\">\n" +
          "<tr>\n" +
            "<th>Project Name</th>\n" +
          "</tr>");
		
		for(int i = 0; i < plist.size(); i++) {
			out.print("<tr>\n" + 
						"<td>" + plist.get(i).getName() + "</td>\n" + 
						"<td>edit</td>\n" + 
						"<td>delete</td>\n" +
					"</tr>\n");
		}
		
		out.println("<button type=\"button\" id=\"myBtn\">create new project</button>\n" + 
				"        \n" + 
				"        \n" + 
				"        <!-- create-new-project btn popup window -->\n" + 
				"        <div id=\"myModal\" class=\"modal\">   \n" + 
				"            <div class=\"modal-content\">\n" + 
				"                <span class=\"close\">&times;</span>\n" + 
				"                  <label for=\"pname\">Project name:</label>\n" + 
				"                  <input type=\"text\" id=\"pname\" name=\"pname\"><br><br>\n" + 
				"                  <input type=\"submit\" value=\"Create\" onclick=\"create()\">\n" + 
				"            </div>\n" + 
				"        </div>\n" + 
				"        \n" + 
				"        \n" + 
				"        <!-- create btn onclick-action (create new row in table) -->\n" + 
				"        <script>\n" + 
				"            function create() {\n" + 
				"                //Get the table\n" + 
				"              var table = document.getElementById(\"table\");\n" + 
				"                //Create row in pos 1\n" + 
				"              var row = table.insertRow(1);\n" + 
				"                //Create cols\n" + 
				"              var cell1 = row.insertCell(0);\n" + 
				"              var cell2 = row.insertCell(1);\n" + 
				"              var cell3 = row.insertCell(2);\n" + 
				"                //Add content to cols\n" + 
				"              cell1.innerHTML = document.getElementById(\"pname\").value;\n" + 
				"              cell2.innerHTML = \"edit\";\n" + 
				"              cell3.innerHTML = \"delete\";\n" + 
				"              }\n" + 
				"        </script>\n" + 
				"        \n" + 
				"        \n" + 
				"        <!-- create-new-project btn onclick-action (open popup) -->\n" + 
				"        <script>\n" + 
				"            // Get the modal\n" + 
				"            var modal = document.getElementById(\"myModal\");\n" + 
				"            // Get the button that opens the popup\n" + 
				"            var btn = document.getElementById(\"myBtn\");\n" + 
				"            // Get the <span> element that closes the modal\n" + 
				"            var span = document.getElementsByClassName(\"close\")[0];\n" + 
				"            // When the user clicks on the button, open the popup\n" + 
				"            btn.onclick = function() {\n" + 
				"              modal.style.display = \"block\";\n" + 
				"            }\n" + 
				"            // When the user clicks on <span> (x), close the modal\n" + 
				"            span.onclick = function() {\n" + 
				"              modal.style.display = \"none\";\n" + 
				"            }\n" + 
				"            // When the user clicks anywhere outside of the modal, close it\n" + 
				"            window.onclick = function(event) {\n" + 
				"              if (event.target == modal) {\n" + 
				"                modal.style.display = \"none\";\n" + 
				"              }\n" + 
				"            }\n" + 
				"\n" + 
				"        </script>");
		
		
		} catch (Exception e) {
			out.println("error");
		}
	}
	
	public boolean deleteProject(int projectId) {
		return false;
	}
	
	public boolean assignRole(User user, int projectId, int roleId) {
		return false;
	}
	
	public Project createProject(String projectName) {
		
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    doGet(req, resp);
	}

}
