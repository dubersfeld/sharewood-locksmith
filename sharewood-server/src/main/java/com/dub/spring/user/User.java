package com.dub.spring.user;

import com.dub.spring.common.Role;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {

	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private List<Role> roles = new ArrayList<>();
  
	public User(UserEntity entity) {
		this.username = entity.getUsername();
		this.email = entity.getEmail();
		this.firstName = entity.getFirstName();
		this.lastName = entity.getLastName();
		this.setRoles(entity);
	}
	
	public User() {}

	public User(User user) {
		this(user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRoles());
	}

	public User(String username, String email, String firstName, String lastName, List<Role> roles) {
		this.username = username;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.roles = roles;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		
		return username.equals(user.username)
        && email.equals(user.email)
        && firstName.equals(user.firstName)
        && lastName.equals(user.lastName)
        && roles.equals(user.roles);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username, email, firstName, lastName, roles);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
        .append("username", username)
        .append("email", email)
        .append("firstName", firstName)
        .append("lastName", lastName)
        .append("roles", roles)
        .toString();
	}
	
	private void setRoles(UserEntity entity) {
		String[] eroles = entity.getRoles().split(",");
		
		for (int i = 0; i < eroles.length; i++) {
			for (Role srole  : Role.values()) {
				if (eroles[i].equals(srole.toString())) {
					this.roles.add(srole);
				} 
			}
		}
		
	}
}
