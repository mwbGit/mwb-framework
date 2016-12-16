package com.mwb.framework.security.cas.rest;

import com.mwb.framework.context.RequestContextAccessor;
import com.mwb.framework.context.SessionContextAccessor;
import com.mwb.framework.log.Log;
import com.mwb.framework.security.DefaultUserDetails;
import com.mwb.framework.security.SecurityUtility;
import com.mwb.framework.security.api.ILoginSuccessHandler;
import com.mwb.framework.util.CookieUtility;
import org.jasig.cas.client.session.SessionMappingStorage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/*
 * This filter class load authentication data from CAS server by TGT for REST request
 */
public class CasRestAuthenticationFilter extends GenericFilterBean {
	private static final Log LOG = Log.getLog(CasRestAuthenticationFilter.class);
	
	private static final String TGT_COOKIE_NAME = "CASTGC";
	
	private ICasRestUserDetailService userDetailsService;
	
	private ILoginSuccessHandler loginSuccessHandler;
	
	private SessionMappingStorage sessionMappingStorage;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
		
        if (RequestContextAccessor.isRestRequest()) {
        	String token = CookieUtility.getRawCookie(req, TGT_COOKIE_NAME);

			if (token != null && SessionContextAccessor.getCurrentAccount() == null) {
				loadUserDetails(req, resp, token);
			}
		}
		
		chain.doFilter(req, resp);
	}
	
	private void loadUserDetails(HttpServletRequest req, HttpServletResponse resp, String token) throws IOException, ServletException {
		
		String ticket = userDetailsService.verifyToken(token);
		if (ticket != null) {
			UserDetails userDetails = userDetailsService.loadUserDetails(token, ticket);
			
			processUserDetails(req, resp, userDetails);
			
			recordSession(req, ticket);
		}
	}
	
	private void recordSession(HttpServletRequest request, String ticket) {
        final HttpSession session = request.getSession(true);

        if (LOG.isDebugEnabled()) {
        	LOG.debug("Recording session for token " + ticket);
        }

        try {
        	this.sessionMappingStorage.removeBySessionById(session.getId());
        } catch (final Exception e) {
            // ignore if the session is already marked as invalid.  Nothing we can do!
        }
        sessionMappingStorage.addSessionById(ticket, session);
    }
	
	private void processUserDetails(HttpServletRequest request, HttpServletResponse response, final UserDetails userDetails) 
			throws IOException, ServletException {
		if (userDetails != null) {
			Authentication authentication = new CasRestAuthentication(userDetails);
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			SecurityUtility.createSession((DefaultUserDetails)userDetails);
			
			if (loginSuccessHandler != null) {
				loginSuccessHandler.onSuccess(request, response);
			}
		}
	}
		
	public void setUserDetailsService(
			ICasRestUserDetailService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public void setLoginSuccessHandler(ILoginSuccessHandler loginSuccessHandler) {
		this.loginSuccessHandler = loginSuccessHandler;
	}

	public void setSessionMappingStorage(SessionMappingStorage sessionMappingStorage) {
		this.sessionMappingStorage = sessionMappingStorage;
	}

	@Override
	public void destroy() {
		LOG.debug(getClass() + " destoried.");
	}
}
