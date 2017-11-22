package com.tma.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.springframework.jms.core.MessageCreator;

import com.tma.mainprocessing.SongMainProcessing;
import com.tma.ref.RequestType;
import com.tma.ref.ResponseStatus;
import com.tma.ref.WebResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

public class Producer {

	@Autowired
	SongMainProcessing songMainProcessing;
	private JmsTemplate jmsTemplate;
	private Destination destination;

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public void sendMessage(boolean isCorectedRequest) {
		if (isCorectedRequest) {
			String message = songMainProcessing.listAllSongs().toJsonStr();
			System.out.println("Producer sends " + message);
			jmsTemplate.send(destination, new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					return session.createTextMessage(message);
				}
			});
		} else {
			String message = new WebResponse(RequestType.LIST, ResponseStatus.FAILED, "Malformed Request!", null).toJsonStr();
			System.out.println("Producer sends " + message);
			jmsTemplate.send(destination, new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					return session.createTextMessage(message);
				}
			});
		}
	}
}