package com.mwb.framework.event.monitor;

import com.mwb.framework.log.Log;
import com.mwb.framework.util.CollectionUtility;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;

public class EventMonitorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final static Log LOG = Log.getLog(EventMonitorServlet.class);
	
	private static final String BROKER_URL = "vm://localhost";
	
	private ConnectionFactory connectionFactory;
	
	@Override
	public void init() throws ServletException {
		super.init();
		connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, BROKER_URL);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		Connection connection = null;
		DestinationSource destinationSource = null;
		Session session = null;
        
        try {
        	connection = connectionFactory.createConnection();
        	connection.start();
        	
        	session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
        	destinationSource = ((ActiveMQConnection) connection).getDestinationSource();  
			
			String status = queryDestnationStatus(session, destinationSource);
			
			response.setContentType("text/html");
			response.setCharacterEncoding("utf-8");
			response.getWriter().print(status);
			response.getWriter().flush();
		} catch (JMSException e) {
			LOG.error("JMSException: ", e);
		} catch (IOException e) {
			LOG.error("IOException: ", e);
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (JMSException e) {
					LOG.error("关闭session失败");
				}
			}
			
			if (destinationSource!= null) {
				try {
					destinationSource.stop();
				} catch (Exception e) {
					LOG.error("事件消息监控连接目标源关闭失败.");
				}
			}
			
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					LOG.error("事件消息监控连接关闭失败.");
				}
			}
		}
	}



	private String queryDestnationStatus(Session session, DestinationSource destinationSource) throws JMSException {
		StringBuilder builder = new StringBuilder();
		
		Set<ActiveMQQueue> queues = destinationSource.getQueues();
		if (!CollectionUtility.isEmpty(queues)) {
			for (ActiveMQQueue destnation : queues) {
				Queue replyTo = session.createTemporaryQueue();
				MessageConsumer consumer = session.createConsumer(replyTo);
				MessageProducer producer = session.createProducer(null);
				
				Message msg = session.createMessage();
				producer.send(destnation, msg);
				
				msg.setJMSReplyTo(replyTo);
				
				Queue query = session.createQueue("ActiveMQ.Statistics.Destination." + destnation.getQueueName());
				producer.send(query, msg);
				
				MapMessage reply = (MapMessage) consumer.receive();
				
				builder.append(destnation.getQueueName() + ":</br>");
				for (Enumeration<?> e = reply.getMapNames(); e.hasMoreElements(); ) {
				    String name = e.nextElement().toString();
				    builder.append(name + "=" + reply.getObject(name) + ",&nbsp;");
				}
				builder.append("</br></br>");
			}
		}
		
		return builder.toString();
	}

}
