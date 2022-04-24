package pratica.pd.main;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Servidor {
	 private static final String RPC_QUEUE_NAME = "MinhaFila";
	 	
	 	public static String meuNome() {
	 		return "wellington pessoa de lima filho";
	 	}
	 
	    public static void main(String[] argv) throws Exception {
	        ConnectionFactory factory = new ConnectionFactory();
	        factory.setHost("localhost");

	        try (Connection connection = factory.newConnection();
	             Channel channel = connection.createChannel()) {
	            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
	            channel.queuePurge(RPC_QUEUE_NAME);

	            channel.basicQos(1);

	            System.out.println(" [x] Awaiting RPC requests");

	            Object monitor = new Object();
	            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
	                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
	                        .Builder()
	                        .correlationId(delivery.getProperties().getCorrelationId())
	                        .build();
	                
	                String response = "";
	                
	                try {
	                    String message = new String(delivery.getBody(), "UTF-8");
	                    System.out.println(" [.] MensagemReceived = " + message + ";");
	                    
	                    response = meuNome();
	                    
	                } catch (RuntimeException e) {
	                    System.out.println(" [.] " + e.toString());
	                } finally {
	                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
	                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

	                    synchronized (monitor) {
	                        monitor.notify();
	                    }
	                }
	            };

	            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
	            while (true) {
	                synchronized (monitor) {
	                    try {
	                        monitor.wait();
	                    } catch (InterruptedException e) {
	                        e.printStackTrace();
	                    }
	                }
	            }
	        }
	    }
}
