package application;

import java.util.List;

/**
 * The User class represents a user entity in the system. It contains the user's
 * details such as userName, password, and role.
 */
public class User {
	private int id;
	private String userName;
	private String name;
	private String password;
	private String email;
	private String currentRole;
	private List<String> roles;
	private int preferredAnswer;
	
	// Flag will be true if one-time password is active on user
	private boolean otp;

	// Constructor to initialize a new User object with userName, password, and
	// role.
	public User(int id, String userName, String name, String password, String email, List<String> roles, boolean otp, int preferredAnswer) {
		this.id = id;
		this.userName = userName;
		this.name = name;
		this.password = password;
		this.email = email;
		this.roles = roles;
		this.otp = otp;
		this.preferredAnswer = preferredAnswer;
	}
	
	public User(int id, String userName, String name, String password, String email, List<String> roles, boolean otp) {
		this.id = id;
		this.userName = userName;
		this.name = name;
		this.password = password;
		this.email = email;
		this.roles = roles;
		this.otp = otp;
	}
	
	public User(String userName, String name, String password, String email, List<String> roles, boolean otp) {	
		this.userName = userName;
		this.name = name;
		this.password = password;
		this.email = email;
		this.roles = roles;
		this.otp = otp;
	}

	public User(String username, String name, String password, String email, String currentRole, List<String> roles, boolean otp) {

	}

	public void addRole(String role) {
		roles.add(role);
	}
	
	public int getUserId() {
		return this.id;
	}

	public String getUsername() {
		return this.userName;
	}

	public String getPassword() {
		return this.password;
	}

	public String getEmail() {
		return this.email;
	}

	public String getName() {
		return this.name;
	}

	public String getCurrentRole() {
		return this.currentRole;
	}

	public List<String> getRoles() {
		return this.roles;
	}
	
	public boolean getOTPFlag() {
		return this.otp;
	}
	
	public int getPreferredAnswer() {
		return this.preferredAnswer;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setCurrentRole(String role) {
		this.currentRole = role;
	}
	
	public void setOTPFlag(boolean flag) {
		this.otp = flag;
	}
	
	public void setPreferredAnswer(int preferredAnswer) {
		this.preferredAnswer = preferredAnswer;
	}

	public String toString() {
		return String.format("USER: \n Id: %s, Username: %s, Name: %s, Email: %s, Roles: %s, Preferred Answer: %s", id, userName, name, email, roles, preferredAnswer);
	}
}
