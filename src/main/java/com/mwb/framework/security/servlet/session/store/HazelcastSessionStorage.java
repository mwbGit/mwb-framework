package com.mwb.framework.security.servlet.session.store;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.mwb.framework.bean.context.BeanContextUtility;
import com.mwb.framework.log.Log;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class HazelcastSessionStorage extends AbstractSessionStorage {
	private static final Log LOG = Log.getLog(HazelcastSessionStorage.class);

	private HazelcastInstance hazelcast;
	
	private static IMap<String, Serializable> sessions;
	
	@Override
	public Serializable get(String sessionId) {
		return sessions.get(sessionId);
	}
	
	@Override
	public void put(String sessionId, Serializable session) {
		sessions.put(sessionId, session);
	}
	
	@Override
	public void remove(String sessionId) {
		sessions.remove(sessionId);
	}
	
	public void setHazelcast(HazelcastInstance hazelcast) {
		this.hazelcast = hazelcast;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		sessions = hazelcast.getMap(getSessionStorageName());
		sessions.addEntryListener(new SessionMapEntryListener(), true);
	}
	
	private class SessionMapEntryListener implements 
		EntryAddedListener<String, Serializable>, 
		EntryRemovedListener<String, Serializable>, 
		EntryEvictedListener<String, Serializable> {
		
		private Collection<HttpSessionListener> sessionListeners;

		@Override
		public void entryAdded(EntryEvent<String, Serializable> event) {
			Object value = event.getValue();
			if (value instanceof HttpSession) {
				HttpSession session = (HttpSession)value;

				LOG.debug("Entry added {}", session.getId());

				HttpSessionEvent sessionEvent = new HttpSessionEvent(session);

				Collection<HttpSessionListener> listeners = getSessionListeners();
				for (HttpSessionListener listner : listeners) {
					listner.sessionCreated(sessionEvent);
				}
			}
		}

		@Override
		public void entryRemoved(EntryEvent<String, Serializable> event) {
			Object value = event.getOldValue();
			if (value instanceof HttpSession) {
				HttpSession session = (HttpSession)value;

				LOG.debug("Entry removed {}", session.getId());

				HttpSessionEvent sessionEvent = new HttpSessionEvent(session);

				Collection<HttpSessionListener> listeners = getSessionListeners();
				for (HttpSessionListener listner : listeners) {
					listner.sessionDestroyed(sessionEvent);
				}
			}
		}

		@Override
		public void entryEvicted(EntryEvent<String, Serializable> event) {
			Object value = event.getOldValue();
			if (value instanceof HttpSession) {
				HttpSession session = (HttpSession)value;

				LOG.debug("Entry evicted {}", session.getId());

				HttpSessionEvent sessionEvent = new HttpSessionEvent(session);

				Collection<HttpSessionListener> listeners = getSessionListeners();
				for (HttpSessionListener listner : listeners) {
					listner.sessionDestroyed(sessionEvent);
				}
			}
		}

		private Collection<HttpSessionListener> getSessionListeners() {
			if (sessionListeners == null) {
				Map<String, HttpSessionListener> listeners = BeanContextUtility.getBeansOfType(HttpSessionListener.class);

				sessionListeners = listeners.values();
			}

			return sessionListeners;
		}
	}

}
