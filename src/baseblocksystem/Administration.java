package baseblocksystem;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.User;

import java.util.List;
import java.util.Random;

/**
 * Servlet implementation class Administration. 
 * Constructs a page for administration purpose. 
 * Checks first if the user is logged in and then if it is the administrator. 
 * If that is OK it displays all users and a form for adding new users.
 * 
 *  @author Martin Host
 *  @version 1.0
 */
@WebServlet("/Administration")
public class Administration extends servletBase {
	private static final long serialVersionUID = 1L;
	private static final int PASSWORD_LENGTH = 6;
       
    /**
     * @see servletBase#servletBase()
     */
    public Administration() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    /**
     * generates a form for adding new users
     * @return HTML code for the form
     */
    private String addUserForm() {
    	String html;
    	html = "<p> <form name=" + addQuotes("input");
    	html += " method=" + addQuotes("get");
    	html += "<p> Add user name: <input type=" + addQuotes("text") + " name=" + addQuotes("addname") + '>';    	
    	html += "<input type=" + addQuotes("submit") + "value=" + addQuotes("Add user") + '>';
    	html += "</form>";
    	return html;
    }
    
    /**
     * Checks if a username corresponds to the requirements for user names. 
     * @param name The investigated username
     * @return True if the username corresponds to the requirements
     */
    private boolean checkNewName(String name) {
    	int length = name.length();
    	boolean ok = (length>=5 && length<=10);
    	if (ok)
    		for (int i=0; i<length; i++) {
    			int ci = (int)name.charAt(i);
    			boolean thisOk = ((ci>=48 && ci<=57) || 
    					          (ci>=65 && ci<=90) ||
    					          (ci>=97 && ci<=122));
    			//String extra = (thisOk ? "OK" : "notOK");
    			//System.out.println("bokst:" + name.charAt(i) + " " + (int)name.charAt(i) + " " + extra);
    			ok = ok && thisOk;
    		}    	
    	return ok;
    }
    
    /**
     * Creates a random password.
     * @return a randomly chosen password
     */
    private String createPassword() {
    	String result = "";
    	Random r = new Random();
    	for (int i=0; i<PASSWORD_LENGTH; i++)
    		result += (char)(r.nextInt(26)+97); // 122-97+1=26
    	return result;
    }
    
    
    /**
     * Adds a user and a randomly generated password to the database.
     * @param name Name to be added
     * @return true if it was possible to add the name. False if it was not, e.g. 
     * because the name already exist in the database. 
     */
    private boolean addUser(String name) {
    	boolean resultOk = true;
    	try{
    		String newPassword = createPassword();
    		System.out.println(newPassword);
    		User u = new User(0, name, newPassword, false);
    		dbService.createUser(u);
		} catch (Exception err) {
		    resultOk = false;
		    err.printStackTrace();
		}
    	return resultOk;
    }
    
    /**
     * Deletes a user from the database. 
     * If the user does not exist in the database nothing happens. 
     * @param name name of user to be deleted. 
     */
    private void deleteUser(String name) {
    	try{
			dbService.deleteUserByUsername(name);
		} catch (Exception err) {
		    err.printStackTrace();
		}
    }

	/**
	 * Handles input from the user and displays information for administration. 
	 * 
	 * First it is checked if the user is logged in and that it is the administrator. 
	 * If that is the case all users are listed in a table and then a form for adding new users is shown. 
	 * 
	 * Inputs are given with two HTTP input types: 
	 * addname: name to be added to the database (provided by the form)
	 * deletename: name to be deleted from the database (provided by the URLs in the table)
	 * 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println(getHeader());
		
		String myName = "";
		
		User loggedInUser;
		try {
			loggedInUser = getLoggedInUser(request);
			if (loggedInUser != null)
	    		myName = (String)loggedInUser.getUsername();  // if the name exists typecast the name to a string
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
		
		// check that the user is logged in
		if (!isLoggedIn(request))
			response.sendRedirect("LogIn");
		else
			if (myName.equals("admin")) {
				out.println("<h1>Administration page " + "</h1>");
				
				// check if the administrator wants to add a new user in the form
				String newName = request.getParameter("addname");
				if (newName != null) {
					if (checkNewName(newName)) {
						boolean addPossible = addUser(newName);
						if (!addPossible)
							out.println("<p>Error: Suggested user name not possible to add</p>");
					}	else
						out.println("<p>Error: Suggesten name not allowed</p>");
				}
					
				// check if the administrator wants to delete a user by clicking the URL in the list
				String deleteName = request.getParameter("deletename");
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
				    	String deleteURL = "Administration?deletename="+name;
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
			} else  // name not admin
				response.sendRedirect("functionality.html");
	}

	/**
	 *
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
