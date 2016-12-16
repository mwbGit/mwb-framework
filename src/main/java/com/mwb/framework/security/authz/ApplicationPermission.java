package com.mwb.framework.security.authz;

import org.springframework.security.core.GrantedAuthority;

public class ApplicationPermission implements GrantedAuthority {

    private static final long serialVersionUID = -9119539278152772405L;

    private String permission;

    public ApplicationPermission(String permission) {
        this.permission = permission;
    }

    public String getAuthority() {
        return permission;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((permission == null) ? 0 : permission.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApplicationPermission other = (ApplicationPermission) obj;
		if (permission == null) {
			if (other.permission != null)
				return false;
		} else if (!permission.equals(other.permission))
			return false;
		return true;
	}

}
