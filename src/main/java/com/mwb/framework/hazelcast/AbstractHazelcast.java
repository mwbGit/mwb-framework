package com.mwb.framework.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.mwb.framework.log.Log;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractHazelcast implements DisposableBean {
	private static final Log LOG = Log.getLog(AbstractHazelcast.class);
	
	private static final int MAX_LOCK_WAIT_TIME = 60;
	
	private HazelcastInstance hazelcast;

	public abstract void init() throws Exception;
	
	public String getHolderName(String alias, Class<?> clasz) {
        return clasz.getSimpleName() + alias;
    }
	
	public String getHolderName(String alias) {
		return getClass().getSimpleName() + alias;
	}

	public HazelcastInstance getHazelcast() {
		return hazelcast;
	}
	
	public void setHazelcast(HazelcastInstance hazelcast) {
		this.hazelcast = hazelcast;
	}
	
	protected boolean tryLock(IMap holder, Object key) {
		return holder.tryLock(key);
	}
	
	protected void lock(IMap holder, Object key) {
		try {
			if (!holder.tryLock(key, MAX_LOCK_WAIT_TIME, TimeUnit.SECONDS)) {
				throw new LockTimeoutException("Hazelcast lock failed!");
			}
		} catch (InterruptedException e) {
			LOG.error("Catch an Exception!", e);
			
			throw new LockInterruptedException(e);
		}
	}
	
	protected void unlock(IMap holder, Object key) {
		holder.unlock(key);
	}

	@Override
	public void destroy() throws Exception {
		if (hazelcast != null) {
			hazelcast.shutdown();
		}
	}
}
