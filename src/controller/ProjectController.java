package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Predicate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.java.swing.ui.StatusBar;

import baseblocksystem.servletBase;
import database.ActivityType;
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
	
	private List<Role> roles;
	
	
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
			
		List<Project> plist = dbService.getAllProjects(1); // Hardcode to get projects for user with user_id = 1
		//List<Project> plist = dbService.getAllProjects(getLoggedInUser(req).getUserId()); Can't get user id by logged in user yet.
			
		String pname = req.getParameter("pname");
		String delete = req.getParameter("deleteProjectId");
		String deleteUser = req.getParameter("deleteUserId");
		String projId = req.getParameter("projectId");
		String role = req.getParameter("newRole");
		String userId = req.getParameter("userId");
		
		//String edit
		
		if (pname != null && !pname.isEmpty()) {
		
			Project project = new Project(1, pname);
			project = createProject(project);
			
			Role project_leader = dbService.getAllRoles().stream().filter(r -> r.getRole().equals("Projektledare")).findAny().orElse(null);
			
			System.out.println(project_leader.getRoleId());
			
			if (project_leader != null) {
				dbService.addUserToProject(1, project.getProjectId(),project_leader.getRoleId());
				plist.add(project);
			}
		
		}
		
		if (delete != null && !delete.isEmpty() && (deleteUser == null || deleteUser.isEmpty())) {
			Project projToDelete = plist.stream().filter(p -> p.getName().equals(delete)).findAny().orElse(null);
			if(projToDelete != null) {
				dbService.deleteProject(projToDelete.getProjectId());
				plist.remove(projToDelete);
			}
		}
		
		if ((delete != null && !delete.isEmpty()) && (deleteUser != null || !deleteUser.isEmpty())) {
			dbService.removeUserFromProject(Integer.parseInt(deleteUser), Integer.parseInt(delete));
		}
		
		if ( (userId != null && !userId.isEmpty()) && (projId != null && !projId.isEmpty()) && (role != null && !role.isEmpty()) ) {
			int roleId = getRoleIdFor(role, roles);
			dbService.updateUserProjectRole(Integer.parseInt(userId), Integer.parseInt(projId), roleId);
		}
		
		if (req.getParameter("editProject") != null) { // TODO: MAKE PROJECT LEADER OR ADMIN CHECK HERE
			out.println("<a href=\"projects\" style=\"padding:36px\">BACK</a>"
					+ "<table id=\\\"table\\\"> \r\n" + 
					"<tr>\r\n" + 
					"<th>Username</th>\r\n" + 
					"<th colspan=\\\"4\\\">Settings</th>\r\n" + 
					"</tr>\r\n" + 
					"				\r\n" + 
					getUserFormsForProject(new Project(Integer.parseInt(req.getParameter("editProject")),req.getParameter("editProjectName") ) ) +
					"				\r\n" + 
					"</table>");
			return;
		}
		
		
		out.println("<h2>Projects</h2>\n" +
        "<table id=\"table\">\n" +
          "<tr>\n" +
            "<th>Project Name</th>\n" +
          "<th colspan=\"2\"> Settings </th>\n" +
          "</tr>");
		
		for(int i = 0; i < plist.size(); i++) {
			out.print("<tr>\n" + 
						"<td>" + plist.get(i).getName() + "</td>\n" + 
						"<td><a href=\"projects?editProject=" + plist.get(i).getProjectId()  + "&" + "editProjectName=" + plist.get(i).getName()  +"\"" +  "id=\"editBtn\">edit</a></td>\n" + 
						"<td><a href=\"projects?deleteProjectId=" + plist.get(i).getName() + "\">delete</a></td>\n" +
					"</tr>\n");
		}
		
		out.println("<button type=\"button\" id=\"myBtn\">create new project</button>\n" + 
				"        \n" + 
				"        \n" + 
				"        <!-- create-new-project btn popup window -->\n" + 
				"        <div id=\"myModal\" class=\"modal\">   \n" + 
				"            <div class=\"modal-content\">\n" + 
				"                <span class=\"close1\">&times;</span>\n" + 
				"                  <label for=\"pname\">Project name:</label>\n" +
				"				   <form>" +
				"                  	<input type=\"text\" id=\"pname\" name=\"pname\" pattern=\"^[a-zA-Z0-9]*$\" title=\"Please enter letters and numbers only.\" minlength=\"3\" maxlength=\"20\" required><br><br>\n" + 
				"                  	<input type=\"submit\" value=\"Create\" onclick=\"create();\">\n" + 
				"					</form>\n" +
				"            </div>\n" + 
				"        </div>\n" +
				"		</table>" +
				"        <!-- create-new-project btn onclick-action (open popup) -->\n" + 
				"        <script>\n" + 
				"            var modal = document.getElementById(\"myModal\");\n" + 
				"            var btn = document.getElementById(\"myBtn\");\n" + 
				"            var span = document.getElementsByClassName(\"close1\")[0];\n" + 
				"            btn.onclick = function() {\n" + 
				"              modal.style.display = \"block\";\n" + 
				"            }\n" + 
				"            span.onclick = function() {\n" + 
				"              modal.style.display = \"none\";\n" + 
				"            }\n" + 
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
	
	private String getUserFormsForProject(Project project) {
		StringBuilder sbBuilder = new StringBuilder();
		try {
			List<User> projectUsers = dbService.getAllUsers(project.getProjectId());
			

			for (int i = 0; i < projectUsers.size(); i++) {
				Role role = dbService.getRole(projectUsers.get(i).getUserId(), project.getProjectId());
				sbBuilder.append("<tr>\n");
				sbBuilder.append("<form id = \"user_form" + (i + 1) + "\">\n");
				sbBuilder.append("<td>");
				sbBuilder.append(projectUsers.get(i).getUsername());
				sbBuilder.append("</td>\n");
				sbBuilder.append("<td>\n<input type=\"hidden\" name=\"userId\" value=\"" + projectUsers.get(i).getUserId() + "\">\n</td>\n");
				sbBuilder.append("<td>\n");
				sbBuilder.append("</td>\n");
				sbBuilder.append("<td>\n<input type=\"hidden\" name=\"projectId\" value=\"" + project.getProjectId() + "\">\n</td>\n");
				sbBuilder.append("<td>\n");
				sbBuilder.append("<select id=\"rol_picker\" name=\"newRole\" form=\"user_form" + (i+1) +"\">\n");
				sbBuilder.append(getRoleSelectOptions(role));
				sbBuilder.append("                    </select>\r\n" + 
						"                </td>\r\n" + 
						"            <td><input type=\"submit\" value=\"Update Role\"></td>\r\n" + 
						"        </form>\r\n" + 
						"		<td> | </td>\r\n" + 
						"                <td><a href=\"projects?deleteUserId=" + projectUsers.get(i).getUserId() + "&" + "deleteProjectId=" + project.getProjectId() +"\"" + ">remove from project</a></td>\r\n" + 
						"            </tr>");
			}
			
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return sbBuilder.toString();
	}
	
    private int getRoleIdFor(String name, List<Role> roles) {
    	for (Role role : roles) {
			if (role.getRole().equals(name))
				return role.getRoleId();
		}
    	
    	return 1;
    }
	
	/**
	 * Gets the options in HTML format for the roles.
	 * @return the HTML code for select options.
	 */
	private String getRoleSelectOptions(Role projectRole) {
		StringBuilder sbBuilder = new StringBuilder();
		try {
			roles = dbService.getAllRoles();
			for (Role role : roles) {
				sbBuilder.append("<option value=\"");
				sbBuilder.append(role.getRole());
				if(projectRole.getRoleId() == role.getRoleId())
					sbBuilder.append("\" selected=\"selected\">");
				else
					sbBuilder.append("\">");
				sbBuilder.append(role.getRole());
				sbBuilder.append("</option>\n");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return sbBuilder.toString();
	}
	public boolean deleteProject(int projectId) {
		return false;
	}
	
	public boolean assignRole(User user, int projectId, int roleId) {
		return false;
	}
	
	public Project createProject(Project proj) {
		try {
			return dbService.createProject(proj);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    doGet(req, resp);
	}

}
