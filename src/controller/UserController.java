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
 * @author Ferit B�lezek ( Enter name if you've messed around with this file ;) )
 * @version 1.0
 * 
 */

@WebServlet("/UserPage")
public class UserController extends servletBase {
	private static final int PASSWORD_LENGTH = 6;
	
//	DatabaseService dbService;
	
	public UserController() {
		super();
//		try {
//			DatabaseService dbService = new DatabaseService();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
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
	    		myName = (String)loggedInUser.getUsername();  // if the name exists typecast the name to a string
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
					}	else
						out.println("<p>Error: Suggesten name not allowed</p>");
				}
					
				// check if the administrator wants to delete a user by clicking the URL in the list
				String deleteName = req.getParameter("deletename");
				if (deleteName != null) {
					if (checkNewName(deleteName)) {
						deleteUser(deleteName);
					}	else
						out.println("<p>Error: URL wrong</p>");
				}
				
				try {			    
				    List<User> users = dbService.getAllUsers();
				    out.println("<p>Registered users:</p>");
				    out.println("<table border=" + addQuotes("1") + ">");
				    out.println("<tr><td>NAME</td><td>PASSWORD</td><td></td></tr>");
				    for (User u : users) {
				    	String name = u.getUsername();
				    	String pw = u.getPassword();
				    	String deleteURL = "UserPage?deletename="+name;
				    	String deleteCode = "<a href=" + addQuotes(deleteURL) +
				    			            " onclick="+addQuotes("return confirm('Are you sure you want to delete "+name+"?')") + 
				    			            "> delete </a>";
				    	
				    	
				    	if (name.equals("admin")) 
				    		deleteCode = "";
				    	out.println("<tr>");
				    	out.println("<td>" + name + "</td>");
				    	out.println("<td>" + pw + "</td>");
				    	out.println("<td>" + deleteCode + "</td>");
				    	out.println("</tr>");
				    }
				    out.println("</table>");
				} catch (SQLException ex) {
				    System.out.println("SQLException: " + ex.getMessage());
				    System.out.println("SQLState: " + ex.getSQLState());
				    System.out.println("VendorError: " + ex.getErrorCode());
				}
				out.println(addUserForm());
				
				out.println("<p><a href =" + addQuotes("functionality.html") + "> Functionality selection page </p>");
				out.println("<p><a href =" + addQuotes("LogIn") + "> Log out </p>");
				out.println("</body></html>");
			} else {}  // name not admin
				//resp.sendRedirect("functionality.html");
		
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
	
    
    private boolean addUser(String name) {
    	boolean resultOk = true;
    	try{
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
    
    private String generatePassword() {
    	String result = "";
    	Random r = new Random();
    	for (int i=0; i<PASSWORD_LENGTH; i++)
    		result += (char)(r.nextInt(26)+97); // 122-97+1=26
    	return result;
    }
    
    private void deleteUser(String name) {
    	try{
			dbService.deleteUserByUsername(name);
		} catch (Exception err) {
		    err.printStackTrace();
		}
    }
    

    private boolean checkNewName(String name) {
    	int length = name.length();
    	boolean isOk = (length>=5 && length<=10);
    	if (isOk)
    		for (int i=0; i<length; i++) {
    			int ci = (int)name.charAt(i);
    			boolean thisOk = ((ci>=48 && ci<=57) || 
    					          (ci>=65 && ci<=90) ||
    					          (ci>=97 && ci<=122));
    			isOk = isOk && thisOk;
    		}    	
    	return isOk;
    }

}
