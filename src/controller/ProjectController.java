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
import database.Role;
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

@WebServlet("/projects")
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
			
		String pname = req.getParameter("pname");
		
		if (pname != null && !pname.isEmpty()) {
		
			Project project = new Project(1, pname);
			project = dbService.createProject(project);
			
			Role project_leader = dbService.getAllRoles().stream().filter(r -> r.getRole().equals("Projektledare")).findAny().orElse(null);
			
			System.out.println(project_leader.getRoleId());
			
			if (project_leader != null) {
				dbService.addUserToProject(1, project.getProjectId(),project_leader.getRoleId());
			}
		
		}
			
		//List<Project> plist = dbService.getAllProjects(getLoggedInUser(req).getUserId()); Can't get user id by logged in user yet.
		
		List<Project> plist = dbService.getAllProjects(1); // Hardcode to get projects for user with user_id = 1
		
		out.println("<h2>Projects</h2>\n" +
        "<table id=\"table\">\n" +
          "<tr>\n" +
            "<th>Project Name</th>\n" +
          "</tr>");
		
		for(int i = 0; i < plist.size(); i++) {
			out.print("<tr>\n" + 
						"<td>" + plist.get(i).getName() + "</td>\n" + 
						"<td>edit</td>\n" + 
						"<td><a href=\"projects?delete=" + plist.get(i).getName() + "\">delete</a></td>\n" +
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
				"				   <form>" +
				"                  	<input type=\"text\" id=\"pname\" name=\"pname\" pattern=\"^[a-zA-Z0-9]*$\" title=\"Please enter letters and numbers only.\" minlength=\"3\" maxlength=\"20\" required><br><br>\n" + 
				"                  	<input type=\"submit\" value=\"Create\" onclick=\"create();\">\n" + 
				"					</form>" +
				"            </div>\n" + 
				"        </div>\n" +
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
			e.printStackTrace();
		}
	}
	
	public boolean deleteProject(int projectId) {
		return false;
	}
	
	public boolean assignRole(User user, int projectId, int roleId) {
		return false;
	}
	
	public Project createProject(String projectName) {
		return null;
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    doGet(req, resp);
	}

}
