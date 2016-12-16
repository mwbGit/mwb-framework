package com.mwb.framework.service.aop.security;

import com.mwb.framework.context.SessionContextAccessor;
import com.mwb.framework.context.model.AccountCO;
import com.mwb.framework.service.aop.security.annotation.RequireAuthentication;
import com.mwb.framework.service.aop.security.annotation.RequiredPermissions;
import com.mwb.framework.util.ReflectionUtility;
import org.apache.commons.lang.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

public class ServiceInvocationAuthorizer implements IServiceInvocationAuthorizer {

	private IServiceLocationAuthorizer serviceLocationAuthorizer;
	
	@Override
	public void authorize(Method method) throws PermissionDeniedException {
		// 如果添加了RequireAuthentication
		Annotation requireAuthenticationAnno = ReflectionUtility.getAnnotationFromInterface(method.getDeclaringClass(), RequireAuthentication.class);
		if (requireAuthenticationAnno != null) {
			AccountCO account = SessionContextAccessor.getCurrentAccount();
			if (account == null) {
				throw new NoLoginSessionException("No login session!");
			}
			
			if (serviceLocationAuthorizer != null && !serviceLocationAuthorizer.authorize(account.getLocations())) {
				throw new LocationDeniedException(
						"User does not have location permission to call " + method.getName());
			}
		}
		
		Annotation requiredPermissionsAnno = ReflectionUtility.getMethodAnnotationFromInterface(method, RequiredPermissions.class);
		if (requiredPermissionsAnno != null) {
			RequiredPermissions requiredPermissions = (RequiredPermissions)requiredPermissionsAnno;
			
			String[] values = requiredPermissions.value();
			
			if (isPermitAll(values)) {
				AccountCO account = SessionContextAccessor.getCurrentAccount();
				if (account == null) {
					throw new NoLoginSessionException("No login session!");
				}
				
				if (serviceLocationAuthorizer != null && !serviceLocationAuthorizer.authorize(account.getLocations())) {
					throw new LocationDeniedException(
							"User does not have location permission to call " + method.getName());
				}
			} else if(!hasAuthority(values)) {
				throw new PermissionDeniedException(
						"User does not have permission to call " + method.getName());
			}
		}
	}

	private boolean isPermitAll(String[] permissions) {
		if (permissions == null || permissions.length == 0) {
			return true;
		}
		
		if (permissions.length == 1 && StringUtils.isBlank(permissions[0])) {
			return true;
		}
		
		return false;
	}
	
	private boolean hasAuthority(String [] requiredPermissions) {
		Set<String> permissons = SessionContextAccessor.getPermissions();
		
		if (permissons != null && requiredPermissions != null) {
			for (String requiredPermission : requiredPermissions) {
				if (permissons.contains(requiredPermission)) {
					return true;
				}
			}
		}
		
		return false;
	}

	public void setServiceLocationAuthorizer(
			IServiceLocationAuthorizer serviceLocationAuthorizer) {
		this.serviceLocationAuthorizer = serviceLocationAuthorizer;
	}
}
