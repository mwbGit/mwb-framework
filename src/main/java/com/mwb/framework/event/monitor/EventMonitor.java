package com.mwb.framework.event.monitor;

import com.mwb.framework.log.Log;
import com.mwb.framework.util.CollectionUtility;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.jms.*;
import java.util.Enumeration;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class EventMonitor implements InitializingBean, DisposableBean {
	
	private final static Log LOG = Log.getLog(EventMonitor.class);
	
	private final static String BROKER_URL = "vm://localhost";
	
	private final static int INTERVAL_IN_MILLISECOND = 300*1000;
	private Timer timer;
	
	private Connection connection;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, BROKER_URL);
			connection = connectionFactory.createConnection();
			connection.start();
			
			timer = new Timer();
			timer.schedule(new EventMonitorTimerTask(connection), 0, INTERVAL_IN_MILLISECOND);
			
			LOG.info("事件消息监控定时器启动.");
		} catch (Exception e) {
			LOG.error("事件消息监控定时器启动失败.");
			throw e;
		}
	}
	
	@Override
	public void destroy() throws Exception {
		if (timer != null) {
			try {
				timer.cancel();
				LOG.info("事件消息监控定时器销毁.");
			} catch (Exception e) {
				LOG.error("事件消息监控定时器销毁失败.");
			}
		}
		
		if (connection != null) {
			DestinationSource destinationSource = ((ActiveMQConnection) connection).getDestinationSource();
			if (destinationSource!= null) {
				try {
					destinationSource.stop();
				} catch (Exception e) {
					LOG.error("事件消息监控连接目标源关闭失败.");
				}
			}
					
			try {
				connection.close();
			} catch (Exception e) {
				LOG.error("事件消息监控连接关闭失败.");
			}
		}
	}
	
	public static class EventMonitorTimerTask extends TimerTask {

		private final static Log LOG = Log.getLog(EventMonitorTimerTask.class);	
		
		private final Connection connection;
		
		private final DestinationSource destinationSource;  
		
		public EventMonitorTimerTask(Connection connection) throws JMSException {
			this.connection = connection;
			this.destinationSource = ((ActiveMQConnection) connection).getDestinationSource();  
		}

		@Override
		public void run() {
			Session session = null;
			
			try {
				session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);

				String status = queryDestnationStatus(session, destinationSource);
				
				LOG.debug(status);
				
			} catch (Exception e) {
				LOG.error("事件消息监控定时器执行失败");
			} finally {
				if (session != null) {
					try {
						session.close();
					} catch (JMSException e) {
						LOG.error("关闭session失败");
					}
				}
			}
		}
		
		private String queryDestnationStatus(Session session, DestinationSource destinationSource) throws JMSException {
			StringBuilder builder = new StringBuilder();
			
			MessageConsumer consumer = null;
			MessageProducer producer = null;
			try {
				Set<ActiveMQQueue> queues = destinationSource.getQueues();
				Queue replyTo = session.createTemporaryQueue();
				consumer = session.createConsumer(replyTo);
				producer = session.createProducer(null);
				
				if (!CollectionUtility.isEmpty(queues)) {
					for (ActiveMQQueue destnation : queues) {
						Message msg = session.createMessage();
						producer.send(destnation, msg);
						
						msg.setJMSReplyTo(replyTo);
						
						Queue query = session.createQueue("ActiveMQ.Statistics.Destination." + destnation.getQueueName());
						producer.send(query, msg);
						
						MapMessage reply = (MapMessage) consumer.receive();
						
						builder.append("queue-" + destnation.getQueueName() + "统计信息为: ");
						for (Enumeration<?> e = reply.getMapNames(); e.hasMoreElements(); ) {
						    String name = e.nextElement().toString();
						    builder.append(name + "=" + reply.getObject(name) + ", ");
						}
						builder.append("\n");
					}
				}
			} finally {
				if (consumer != null) {
					consumer.close();
				}
				
				if (producer != null) {
					producer.close();
				}
			}
			
			return builder.toString();
		}
	}

}
