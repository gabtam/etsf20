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

import com.sun.beans.introspect.PropertyInfo.Name;

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
	
	
	private List<Role> roles;
	
	private Project currentProject;
	
	
	public ProjectController() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		String sessionInfoUserName = "null";
		String sessionInfoProjectName = "null";
		try {
			sessionInfoUserName = getLoggedInUser(req) == null ? "null" : getLoggedInUser(req).getUsername();
			int sessionInfoProject = getProjectId(req);
			Project p = dbService.getProject(sessionInfoProject);
			if(sessionInfoProject != 0 && p != null)
				sessionInfoProjectName = p.getName();
			else
				sessionInfoProjectName = "null";

			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		out.println("<body>" + "<link rel=\"stylesheet\" type=\"text/css\" href=\"StyleSheets/ProjectController.css\">\n");
		out.println(getHeader(req));
		out.println(
				"        <div id=\"wrapper\">\r\n" + 
						getNav(req) +
				"            <div id=\"bodyContent\">");
		
		try {
			
		List<Project> plist = dbService.getAllProjects(getLoggedInUser(req).getUserId());
		
		String pname = req.getParameter("pname");
		String delete = req.getParameter("deleteProjectId");
		String deleteUser = req.getParameter("deleteUserId");
		String projId = req.getParameter("projectId");
		String newRole = req.getParameter("newRole");
		String userId = req.getParameter("userId");
		String initRole = req.getParameter("role");
		String projectSelected = req.getParameter("projectSelected");
		
		
		//String edit
		
		
		if (projectSelected != null ) {
			setProjectId(req, Integer.valueOf(projectSelected));
			resp.sendRedirect("UserPage");
		}
		
		
		if (pname != null && !pname.isEmpty()) {
		
			Project project = new Project(1, pname);
			project = createProject(project);
			
			Role project_leader = dbService.getAllRoles().stream().filter(r -> r.getRole().equals("Projektledare")).findAny().orElse(null);
			
			if (project_leader != null) {
				dbService.addUserToProject(1, project.getProjectId(),project_leader.getRoleId());
				out.println("<p style=\"background-color:#16a085;color:white;padding:16px;\">SUCCESFULLY CREATED PROJECT: " + pname + "</p>");
				plist.add(project);
			}
		
		}
		
		if ( delete != null && !delete.isEmpty() && (deleteUser == null || deleteUser.isEmpty())) {
			Project projToDelete = plist.stream().filter(p -> p.getName().equals(delete)).findAny().orElse(null);
			if(projToDelete != null && actionIsAllowed(req, Integer.valueOf(projToDelete.getProjectId()))) {
				deleteProject(projToDelete.getProjectId());
				out.println("<p style=\"background-color:#16a085;color:white;padding:16px;\">SUCCESFULLY DELETED PROJECT:" + projToDelete.getName() + "</p>");
				plist.remove(projToDelete);
			} else {
				out.println("<p style=\"background-color:#c0392b;color:white;padding:16px;\">FAILED TO DELETE PROJECT:" + projToDelete.getName() +" REASON: You are not allowed to perform this action." +  "</p>");
			}
		}
		
		if ((delete != null && !delete.isEmpty()) && (deleteUser != null && !deleteUser.isEmpty()) ) {
			dbService.removeUserFromProject(Integer.parseInt(deleteUser), Integer.parseInt(delete));
			out.println("<p style=\"background-color:#16a085;color:white;padding:16px;\">SUCCESFULLY DELETED USER</p>");
		}
		
		if ( (userId != null && !userId.isEmpty()) && (projId != null && !projId.isEmpty()) && (newRole != null && !newRole.isEmpty()) ) {
			int roleId = getRoleIdFor(newRole, roles);
			dbService.updateUserProjectRole(Integer.parseInt(userId), Integer.parseInt(projId), roleId);
			out.println("<p style=\"background-color:#16a085;color:white;padding:16px;\">SUCCESFULLY UPDATED ROLE TO: " + newRole +  "</p>");
		}
		
		if ((userId != null && !userId.isEmpty()) && (projId != null && !projId.isEmpty()) && (initRole != null && !initRole.isEmpty())) {
			List<User> allUsers = dbService.getAllUsers();
			User user = allUsers.stream().filter(u -> u.getUsername().equals(userId)).findAny().orElse(null);
			
			if (user == null) {
				out.println("<p style=\"background-color:#c0392b;color:white;padding:16px;\">FAILED TO ADD USER: " + userId + ", reason: user does not exist.</p>");
			} else {
				
				User sameUser = dbService.getAllUsers(Integer.parseInt(projId)).stream().filter(u -> u.getUsername().equals(userId)).findAny().orElse(null);
				
				if (sameUser == null) {
					User findUser = allUsers.stream().filter(u -> u.getUsername().equals(userId)).findAny().orElse(null);
					
					dbService.addUserToProject(findUser.getUserId(), Integer.parseInt(projId), getRoleIdFor(initRole, roles));
					out.println("<p style=\"background-color:#16a085;color:white;padding:16px;\">ADDED USER: " + userId +  "</p>");
				} else {
					out.println("<p style=\"background-color:#c0392b;color:white;padding:16px;\">FAILED TO ADD USER: " + userId + ", reason: user is already in the project.</p>");
				}
			}
		}
		
		

		if (req.getParameter("editProject") != null && actionIsAllowed(req, Integer.valueOf(req.getParameter("editProject")))) {
			Project p = new Project(Integer.parseInt(req.getParameter("editProject")),req.getParameter("editProjectName") );
			currentProject = p;
			out.println("<a href=\"projects\" style=\"padding:36px\">BACK</a>"
					+ "<h2>Add new user to project:</h2>\r\n" + 
					"<form>\r\n" + 
					"<table id=\"table\">\r\n" + 
					"<tr>\r\n" + 
					"<td><label for=\"pname\">enter username:</label>\r\n" + 
					"<input type=\"text\" id=\"pname\" name=\"userId\" pattern=\"^[a-zA-Z0-9]*$\" title=\"Please enter letters and numbers only.\" minlength=\"3\" maxlength=\"20\" required><br><br>\r\n" + 
					"</td>\r\n" +
					"<td>\r\n" + 
					"<label for=\"rol_picker\">Pick role:<label>\r\n" + 
					"<select id=\"rol_picker\" name=\"role\" form=\"user_form1\">\r\n" + 
					 getRoleSelectOptions() +
					"                    </select>\r\n" + 
					"</td>\r\n" +
					"<td>\n<input type=\"hidden\" name=\"projectId\" value=\"" + currentProject.getProjectId() + "\">\n</td>\n" +
					"<td> \r\n" + 
					"<input type=\"submit\" value=\"Add user\">\r\n" + 
					"</td>" +
					"</tr>\r\n" + 
					"</table>\r\n" + 
					"</form><br>\r\n" + 
					"<h2>Active users in the project</h2>" +
					"<table id=\"table\"> \r\n" + 
					"<tr>\r\n" + 
					"<th>Username</th>\r\n" + 
					"<th colspan=\"6\">Settings</th>\r\n" + 
					"</tr>\r\n" + 
					"				\r\n" + 
					getUserFormsForProject(p) +
					"				\r\n" + 
					"</table>");
			return;
		} else if (req.getParameter("editProject") != null && !actionIsAllowed(req, Integer.valueOf(req.getParameter("editProject")))) {
			out.println("<p style=\"background-color:#c0392b;color:white;padding:16px;\">ACTION NOT ALLOWED: " + ", reason: You are not an admin or a projectleader for this project.</p>");
		}
		
		
		out.println("<h2>Projects</h2>\n" +
        "<table id=\"table\">\n" +
          "<tr>\n" +
            "<th>Project Name</th>\n" +
          "<th colspan=\"2\"> Settings </th>\n" +
          "</tr>");
		
		for(int i = 0; i < plist.size(); i++) {
			out.print("<tr>\n" + 
						"<td><a href=\"projects?projectSelected=" + plist.get(i).getProjectId() + "\">" + plist.get(i).getName() + "</a></td>\n" + 
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
		out.println("            </div>\r\n" + 
				"    </div>\r\n" + 
				"\r\n" + 
				"</body>");
	}
	
	private boolean actionIsAllowed(HttpServletRequest req, int projectId) {
		try {
			User user = getLoggedInUser(req);
			
			if (user == null)
				return false;
			
			if (user.isAdmin())
				return true;
			else {
				int val = dbService.getProjectUserIdByUserIdAndProjectId(user.getUserId(), projectId);
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
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

	
	
	/**
	 * Gets the options in HTML format for the roles.
	 * @return the HTML code for select options.
	 */
	private String getRoleSelectOptions() {
		StringBuilder sbBuilder = new StringBuilder();
		try {
			List<Role> roles = dbService.getAllRoles();
			for (Role role : roles) {
				sbBuilder.append("<option value=\"");
				sbBuilder.append(role.getRole());
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
		try {
			dbService.deleteProject(projectId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public Project createProject(String projectName) throws SQLException {
		int newId;
		Project newProject;
		newId = dbService.getAllProjects().get(dbService.getAllProjects().size()).getProjectId()+1;
		newProject = new Project(newId, projectName);
		if(!dbService.getAllProjects().contains(newProject)) 
		{
			dbService.createProject(new Project(newId, projectName));
			return newProject;
		}
		return null;
	}

}
