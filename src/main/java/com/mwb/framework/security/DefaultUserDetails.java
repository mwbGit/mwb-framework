package com.mwb.framework.security;

import com.mwb.framework.context.model.AccountCO;
import com.mwb.framework.security.authz.ApplicationPermission;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	private AccountCO account;

	private List<ApplicationPermission> authorities;

	public DefaultUserDetails(
			AccountCO account) {
		
		this.account = account;
		
		this.authorities = new ArrayList<ApplicationPermission>();
		for (String s : account.getPermissions()) {
			ApplicationPermission permission = new ApplicationPermission(s);
			authorities.add(permission);
		}
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return account.getName();
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

	public AccountCO getAccount() {
		return account;
	}
}
