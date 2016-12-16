package com.mwb.framework.security.cas;

import com.hazelcast.core.IMap;
import com.mwb.framework.hazelcast.AbstractHazelcast;
import com.mwb.framework.security.servlet.session.store.AbstractSessionStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.client.session.SessionMappingStorage;

import javax.servlet.http.HttpSession;

public class HazelcastSessionMappingStorage extends AbstractHazelcast implements SessionMappingStorage {
	private final Log log = LogFactory.getLog(getClass());

	/**
     * Maps the ID from the CAS server to the Session.
     */
    private IMap<String, String> MANAGED_SESSIONS;

    /**
     * Maps the Session ID to the key from the CAS Server.
     */
    private IMap<String, String> ID_TO_SESSION_KEY_MAPPING;
    
    private AbstractSessionStorage sessionStorage;
    
    @Override
	public void init() throws Exception {
		MANAGED_SESSIONS = getHazelcast().getMap(getHolderName("MANAGED_SESSIONS"));
		ID_TO_SESSION_KEY_MAPPING = getHazelcast().getMap(getHolderName("ID_TO_SESSION_KEY_MAPPING"));
	}

	@Override
	public synchronized HttpSession removeSessionByMappingId(String mappingId) {
		HttpSession session = null;
		
		String sessionId = (String) MANAGED_SESSIONS.get(mappingId);
		if (sessionId != null) {
			
			session = (HttpSession) sessionStorage.get(sessionId);
			
			removeBySessionById(sessionId);	        
		}
		
        return session;
	}

	@Override
	public synchronized void removeBySessionById(String sessionId) {
		if (log.isDebugEnabled()) {
            log.debug("Attempting to remove Session=[" + sessionId + "]");
        }

        final String key = (String) ID_TO_SESSION_KEY_MAPPING.get(sessionId);

        if (log.isDebugEnabled()) {
            if (key != null) {
                log.debug("Found mapping for session.  Session Removed.");
            } else {
                log.debug("No mapping for session found.  Ignoring.");
            }
        }
        
        if (key != null) {
        	MANAGED_SESSIONS.remove(key);
        }
        ID_TO_SESSION_KEY_MAPPING.remove(sessionId);
	}

	@Override
	public synchronized void addSessionById(String mappingId, HttpSession session) {
		ID_TO_SESSION_KEY_MAPPING.put(session.getId(), mappingId);
        MANAGED_SESSIONS.put(mappingId, session.getId());
	}

	public void setSessionStorage(AbstractSessionStorage sessionStorage) {
		this.sessionStorage = sessionStorage;
	}
}
