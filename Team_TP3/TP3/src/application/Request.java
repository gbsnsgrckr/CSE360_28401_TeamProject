package application;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
/**
 * This is a class to create a request class, which holds the request, the user who created it,
 * whether the request has been accepted/declined by an instructor, and if the request has been
 * accepted/declined by an admin
 * @author Darren Fernandes
 */
public class Request {
	private StringProperty request;
	private User user;
	private boolean requestTOF;
	private boolean requestATOF;
	/**
	 * this is a constructor to create a basic request, which auto sets both parameters 
	 * for the true or false booleans to false, allowing it to be set up in the database
	 * @param request
	 * @param user
	 */
	public Request(String request, User user) {
		this.request = new SimpleStringProperty(request);
		this.user = user;
	}
	/**
	 * This constructor is for getting information from the database and correctly populating the 
	 * fields required in the instructor and admin page
	 * @param request - the request
	 * @param user - the user
	 * @param requestTOF - if it instructor has seen and accepted/declined it
	 * @param requestATOF - if the admin has accepted/declined it
	 */
	public Request(String request, User user, boolean requestTOF, boolean requestATOF) {
		this.request = new SimpleStringProperty(request);
		this.user = user;
		this.requestTOF = requestTOF;
		this.requestATOF = requestATOF;
	}
	/**
	 * getter
	 * @return
	 */
	public StringProperty requestProperty() {
        return request;
    }
	/**
	 * getter
	 * @return
	 */
	public String getRequest() {
		return request.get();
	}
	/**
	 * getter
	 * @return
	 */
	public boolean getRequestTOF() {
		return requestTOF;
	}
	/**
	 * getter
	 * @return
	 */
	public boolean getRequestATOF() {
		return requestATOF;
	}
	/**
	 * setter
	 * @return
	 */
	public void setRequest(String request) {
		this.request.set(request);
	}
	/**
	 * setter
	 * @return
	 */
	public String getUserName() {
        return user.getUsername();
    }

	/**
	 * setter
	 * @return
	 */
	public void setRequestTOF(boolean tof) {
		this.requestTOF = tof;
	}
	/**
	 * setter
	 * @return
	 */
	public void setRequestATOF(boolean tof) {
		this.requestATOF = tof;
	}
	/**
	 * setter
	 * @return
	 */
	public User getUser() {
		return user;
	}
	}