package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

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
 * @author Ferit Bölezek ( Enter name if you've messed around with this file ;) )
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
		
		out.println("<body>");
				
	}
	
	public boolean deleteProject(int projectId) throws Exception {
		if(dbService.getAllProjects().contains(dbService.getProject(projectId))) 
		{
			dbService.deleteProject(projectId);
			return true;
		}
		return false;
	}
	
	public boolean assignRole(User user, int projectId, int roleId) throws Exception {
		if(dbService.getAllProjects().contains(dbService.getProject(projectId)) 
				&& dbService.getAllUsers().contains(user)) 
		{
			for(int i=0; i< dbService.getAllRoles().size() ; i++) 
			{
				if(dbService.getAllRoles().get(i).getRoleId()==roleId) 
				{
					dbService.updateUserProjectRole(user.getUserId(), projectId, roleId);
					return true;
				}
			}
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
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    doGet(req, resp);
	}

}
