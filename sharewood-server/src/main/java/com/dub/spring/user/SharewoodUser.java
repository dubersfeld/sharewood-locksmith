package com.dub.spring.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class SharewoodUser extends User implements UserDetails {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String ROLE_PREFIX = "ROLE_";

	public SharewoodUser(User user) {
		super(user);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return getRoles().stream()
        .map(Enum::name)
        .map(String::toUpperCase)
        .map(rn -> ROLE_PREFIX + rn)
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return "n/a";
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
}
