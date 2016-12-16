package com.mwb.framework.jms;

import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.springframework.beans.factory.InitializingBean;

public class ActiveMQConnectionFactory 
	extends org.apache.activemq.spring.ActiveMQConnectionFactory
	implements InitializingBean {

	private Integer queuePrefetch;
	private Integer topicPrefetch;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
		ActiveMQPrefetchPolicy prefetchPolicy = new ActiveMQPrefetchPolicy();
		
		if (queuePrefetch != null) {
			prefetchPolicy.setQueuePrefetch(queuePrefetch);
		}
		
		if (topicPrefetch != null) {
			prefetchPolicy.setTopicPrefetch(topicPrefetch);
		}
		
		setPrefetchPolicy(prefetchPolicy);
		
		super.afterPropertiesSet();
	}

	public void setQueuePrefetch(Integer queuePrefetch) {
		this.queuePrefetch = queuePrefetch;
	}

	public void setTopicPrefetch(Integer topicPrefetch) {
		this.topicPrefetch = topicPrefetch;
	}
}
