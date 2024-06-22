package Messaging;



import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.*;


@Startup
@Singleton
public class JMSClient {
@Resource
(mappedName="java:/jms/queue/MyTrelloQueue")
private Queue MyTrelloQueue;
@Inject
JMSContext context;
public void sendMessage( String msg)
{
	try
	{
		JMSProducer producer=context.createProducer();
		TextMessage message =context.createTextMessage(msg);
		producer.send(MyTrelloQueue, message);
		System.out.println("Sent Message:"+msg);
		
	}catch(Exception e)
	{
		e.printStackTrace();
	}
}
public String getMessage()
{
	JMSConsumer consumer=context.createConsumer(MyTrelloQueue);
	try
	{
		TextMessage msg=(TextMessage) consumer.receiveNoWait();
		if(msg!=null)
		{
			System.out.println("Recieved Message:"+msg);
			return msg.getBody(String.class);
		}
		else
		{
			return null;
		}
	}catch(Exception e)
	{
		e.printStackTrace();
		return null;
	}finally
	{
		consumer.close();
	}
	}
}
