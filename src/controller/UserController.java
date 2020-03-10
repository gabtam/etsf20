package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import baseblocksystem.servletBase;
import database.*;

/**
 * Servlet implementation class UserController
 * 
 * A xx page.
 * 
 * Description of the class.
 * 
 * @author Ferit B�lezek ( Enter name if you've messed around with this file
 *         ;) )
 * @version 1.0
 * 
 */
//typklar
@WebServlet("/UserPage")
public class UserController extends servletBase {
	private static final int PASSWORD_LENGTH = 6;

	public UserController() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = resp.getWriter();
		out.println(getHeader());

		String myName = "";

		User loggedInUser;
		try {
			loggedInUser = getLoggedInUser(req);
			if (loggedInUser != null)
				myName = (String) loggedInUser.getUsername(); // if the name exists typecast the name to a string
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// check that the user is logged in
		if (!isLoggedIn(req))
			resp.sendRedirect("LogIn");
		else

		if (true) {
			out.println("<h1>User Page " + "</h1>");

			// check if the administrator wants to add a new user in the form
			String newName = req.getParameter("addname");
			if (newName != null) {
				if (checkNewName(newName)) {
					boolean addPossible = addUser(newName);
					if (!addPossible)
						out.println("<p>Error: Suggested user name not possible to add</p>");
				} else
					out.println("<p>Error: Suggesten name not allowed</p>");
			}

			// check if the administrator wants to delete a user by clicking the URL in the
			// list

			String deleteName = req.getParameter("deletename");
			if (deleteName != null) {
				if (checkNewName(deleteName)) {
					deleteUser(deleteName);
				} else
					out.println("<p>Error: URL wrong</p>");
			}

			String assignId = req.getParameter("selname");
			String assignProject = req.getParameter("selproject");
			String assignRole = req.getParameter("selrole");
			if (assignId != null) {
				int id = Integer.parseInt(assignId);
				int proj =Integer.parseInt(assignProject);
				int role = Integer.parseInt(assignRole);
				addUserToProject(id, proj, role);
			}
			


			try {
				List<User> users = dbService.getAllUsers();
				List<Project> projects = dbService.getAllProjects();
				List<Role> roles = dbService.getAllRoles();
				out.println("<p>Registered users:</p>");
				out.println("<table border=" + addQuotes("1") + ">");
				out.println("<tr><td>NAME</td><td></td><td>SELECT PROJECT</td><td>SELECT ROLE</td><td></td></tr>");
				for (User u : users) {
					String name = u.getUsername();
					String deleteURL = "UserPage?deletename=" + name;
					String deleteCode = "<a href=" + addQuotes(deleteURL) + " onclick="
							+ addQuotes("return confirm('Are you sure you want to delete " + name + "?')")
							+ "> delete </a>";
					String projectList = "<form name="+ addQuotes("rList" + u.getUserId())+ "method=" + addQuotes("get")+
							"> <input list=" + addQuotes("pList") + " name="
							+ addQuotes("pList" + u.getUserId()) + "> <datalist id=" + addQuotes("pList") + ">";
					for (Project p : projects) {
						projectList += "<option value =" + addQuotes(p.getName()) + ">";
					}
					projectList += "</datalist> </form>";

					String roleList = "<form name="+ addQuotes("rList" + u.getUserId())+ "method=" + addQuotes("get")+
							"> <input list=" + addQuotes("rList") + "> <datalist id=" + addQuotes("rList") + ">";
					for (Role r : roles) {
						roleList += "<option value =" + addQuotes(r.getRole()) + ">";
					}
					roleList += "</datalist> </form>";
					String assignURL = "UserPage?assignid=" + u.getUserId();
					String assignCode = "<a href=" + addQuotes(assignURL) + " onclick="
							+ addQuotes("pList" + u.getUserId()) + ".submit()> assign </a>";

					if (u.isAdmin()) {
						deleteCode = "";
						projectList = "";
						roleList = "";
						assignCode = "";
					}

					out.println("<tr>");
					out.println("<td>" + name + "</td>");
					out.println("<td>" + deleteCode + "</td>");
					out.println("<td>" + projectList + "</td>");
					out.println("<td>" + roleList + "</td>");
					out.println("<td>" + assignCode + "</td>");
					out.println("</tr>");
				}
				out.println("</table>");
			} catch (SQLException ex) {
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
			out.println(addUserForm());
			try {
				out.println(assignUserForm());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			out.println("<p><a href =" + addQuotes("functionality.html") + "> Functionality selection page </p>");
			out.println("<p><a href =" + addQuotes("LogIn") + "> Log out </p>");
			out.println("</body></html>");
		} else {

		}
	}

	public String addUserForm() {
		
		String html;
		html = "<p> <form name=" + addQuotes("input");
		html += " method=" + addQuotes("get");
		html += "<p> Add user name: <input type=" + addQuotes("text") + " name=" + addQuotes("addname") + '>';
		html += "<input type=" + addQuotes("submit") + "value=" + addQuotes("Add user") + '>';
		html += "</form>";
		return html;
	}
	
	public String assignUserForm() throws SQLException {
		List<User> users = dbService.getAllUsers();
		List<Project> projects = dbService.getAllProjects();
		List<Role> roles = dbService.getAllRoles();
		String html;
		html = "<p> Assign user to project. <form name="+addQuotes("assign")+" method ="+addQuotes("get")+
				"<p> Name: <select name="+addQuotes("selname")+">";
		for(User u:users) {
			html += "<option value ="+ u.getUserId() + ">"+u.getUsername()+"</option>";
		}
		html += "</select> Project: <select name="+addQuotes("selproject")+">";
		for(Project p:projects) {
			html += "<option value =" + p.getProjectId() + ">"+p.getName()+"</option>";
		}
		html += "</select> Role: <select name="+addQuotes("selrole")+">";
		for(Role r:roles) {
			html += "<option value =" + r.getRoleId() + ">"+r.getRole()+"</option>";
		}
		html += "<input type="+ addQuotes("submit") + "value=" +addQuotes("Assign")+"> </form>";
		return html;
	}

	private boolean addUser(String name) {
		boolean resultOk = true;
		try {
			String newPassword = generatePassword();
			System.out.println(newPassword);
			User u = new User(0, name, newPassword, false);
			dbService.createUser(u);
		} catch (Exception err) {
			resultOk = false;
			err.printStackTrace();
		}
		return resultOk;
	}

	private boolean addUserToProject(int userId, int projectId, int roleId) {
		boolean resultOk = true;
		try {
			dbService.addUserToProject(userId, projectId, roleId);
		} catch (Exception err) {
			resultOk = false;
			err.printStackTrace();
		}
		return resultOk;
	}

	private String generatePassword() {
		String result = "";
		Random r = new Random();
		for (int i = 0; i < PASSWORD_LENGTH; i++)
			result += (char) (r.nextInt(26) + 97); // 122-97+1=26
		return result;
	}

	private void deleteUser(String name) {
		try {
			dbService.deleteUserByUsername(name);
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	private boolean checkNewName(String name) {
		int length = name.length();
		boolean isOk = (length >= 5 && length <= 10);
		if (isOk)
			for (int i = 0; i < length; i++) {
				int ci = (int) name.charAt(i);
				boolean thisOk = ((ci >= 48 && ci <= 57) || (ci >= 65 && ci <= 90) || (ci >= 97 && ci <= 122));
				isOk = isOk && thisOk;
			}
		return isOk;
	}

}
