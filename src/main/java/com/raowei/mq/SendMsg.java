package com.raowei.mq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 */
public class SendMsg {
    private static final String url = "tcp://127.0.0.1:61616";
    private static final String QUEUE_NAME = "choice.queue";
    private String expectedBody = "<hello>world</hello>";

    public void sendMessage() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
        try {
            Connection connection = factory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(QUEUE_NAME);
            MessageProducer producer = session.createProducer(queue);
            TextMessage textMessage = session.createTextMessage(expectedBody);
            textMessage.setStringProperty("headName","remoteB");
            producer.send(textMessage);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        SendMsg sndMsg = new SendMsg();
        try{
            sndMsg.sendMessage();
        }catch(Exception ex){
            System.out.println(ex.toString());
        }
    }
}
