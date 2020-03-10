package controller;

import java.io.IOException;

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
	
	DatabaseService dbService;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}
	
	public String addUserForm() {
		String html = "Enter username for new user";
    	html += "<p> <form name=" + formElement("input");
    	//html += " action=" + formElement(myURL); 
    	html += " method=" + formElement("get");
    	html += "<p> Name: <input type=" + formElement("text") + " name=" + formElement("user") + '>';
    	html += "<p> <input type=" + formElement("submit") + "value=" + formElement("Submit") + '>';
    	return html;
	}
	
    private String formElement(String par) {
    	return '"' + par + '"';
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
