package com.raowei.mq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by Administrator on 2017/5/13.
 */
public class ReceiveMsg {

    private static final String url = "tcp://127.0.0.1:61616";
    private static final String QUEUE_NAME = "choice.queue";

    public void receiveMessage() {

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
        try {
            Connection connection = factory.createConnection();
            Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(QUEUE_NAME);
            MessageConsumer consumer = session.createConsumer(queue);
            this.consumer(connection,consumer,session);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }


    private void consumer(Connection connection,MessageConsumer consumer,Session session) throws JMSException {
        for (int i = 0; i < 1;) {
            Message message = consumer.receive(1000);
            if (message != null) {
                i++;
                onMessage(message);
            }
        }
        System.out.println("Closing connection");
        consumer.close();
        session.close();
        connection.close();
    }


    private void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage txtMsg = (TextMessage) message;
                String msg = txtMsg.getText();
                System.out.println("Received: " + msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        ReceiveMsg msg = new ReceiveMsg();
        msg.receiveMessage();
    }
}
