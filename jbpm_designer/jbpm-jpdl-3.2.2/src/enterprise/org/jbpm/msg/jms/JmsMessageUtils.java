package org.jbpm.msg.jms;

import java.util.Enumeration;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Topic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JmsMessageUtils {
	
	public static String dump(Message message){
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("JMS Message Dump\n")
		    .append("Message type is " + message.getClass().getName() + "\n");
		
		try
		{
		  if (message instanceof ObjectMessage)
		    sb.append("Message object type is " + ((ObjectMessage) message).getObject().getClass().getName() + "\n");
		  sb.append("Reply to " + getDestinationName( message.getJMSReplyTo() ) + "\n");
		  Enumeration e = message.getPropertyNames();
		  while (e.hasMoreElements())
		  {
			  String propertyName = (String) e.nextElement();
			  Object property = message.getObjectProperty(propertyName);
			  sb.append("Property " + propertyName + " value " + property.toString() + "\n");
		  }
		}
		catch(JMSException j)
		{
			log.error("JMS exception while dumping message", j);
		}
				
		return sb.toString();
	}
	
	public static String getDestinationName(Destination d){
		try {
			if (d instanceof Queue)
			  return ((Queue) d).getQueueName();
			else
			if (d instanceof Topic)
			  return ((Topic) d).getTopicName();	
		}
		catch(JMSException j) {};

		return null;
	}

	private static final Log log = LogFactory.getLog( JmsMessageUtils.class );
}
