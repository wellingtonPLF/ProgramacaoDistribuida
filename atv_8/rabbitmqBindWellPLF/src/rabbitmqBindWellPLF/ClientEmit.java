package rabbitmqBindWellPLF;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ClientEmit {

	private static final String exchange_name = "myFirstExchange";

    public static void main(String[] argv) throws Exception {
    	String [] lista = {"getName", "Wellington", "Pessoa"};
    	
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try 
        (
        	Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()
        ) 
        {
            channel.exchangeDeclare(exchange_name, BuiltinExchangeType.DIRECT);

            String severity = getSeverity(lista);
            String message = getMessage(lista);

            channel.basicPublish(exchange_name, severity, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + severity + "':'" + message + "'");
        }
    }

    private static String getSeverity(String[] strings) {
        if (strings.length < 1)
            return "info";
        return strings[0];
    }

    private static String getMessage(String[] strings) {
        if (strings.length < 2)
            return "Hello World!";
        return joinStrings(strings, " ", 1);
    }

    private static String joinStrings(String[] strings, String delimiter, int startIndex) {
        int length = strings.length;
        StringBuilder words = new StringBuilder(strings[startIndex]);
        for (int i = startIndex + 1; i < length; i++) {
            words.append(delimiter).append(strings[i]);
        }
        return words.toString();
    }

}
