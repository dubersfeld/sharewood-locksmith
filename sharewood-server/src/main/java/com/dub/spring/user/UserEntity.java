package com.dub.spring.user;

import com.dub.spring.common.Role;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="sharewooduser")
public class UserEntity {

	private long id;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String roles;

  
	public UserEntity() {}

	public UserEntity(UserEntity user) {
		this(user.id, user.username, user.email, user.firstName, user.username, user.roles);
	}

	public UserEntity(long id, String username, String email, String firstName, String lastName, String roles) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	
	}

    @Column(name = "username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "firstname")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "lastname")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	
    @Column(name = "roles")
	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserEntity user = (UserEntity) o;
		
		return username.equals(user.username)
        && email.equals(user.email)
        && firstName.equals(user.firstName)
        && lastName.equals(user.lastName)
        ;
	}

	@Override
	public int hashCode() {
		return Objects.hash(username, email, firstName, lastName);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
        .append("username", username)
        .append("email", email)
        .append("firstName", firstName)
        .append("lastName", lastName)
        
        .toString();
	}
	
	
}
