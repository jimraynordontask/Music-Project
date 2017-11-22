package com.tma.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

public class Consumer implements MessageListener {

	@Autowired
	Producer producer;

	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = ((TextMessage) message);
		try {
			if (textMessage.getText().equals("list song")) {
				producer.sendMessage(true);
			}else producer.sendMessage(false);
			System.out.println("Request: "+textMessage.getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
