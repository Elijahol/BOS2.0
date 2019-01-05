package cn.itcast.bos.mq;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.springframework.stereotype.Service;

import cn.itcast.bos.utils.MailUtils;

@Service("mailConsumer")
public class MailConsumer implements MessageListener{

	@Override
	public void onMessage(Message message) {
		MapMessage mapMessage = (MapMessage) message;
		try {
			String subject = mapMessage.getString("subject");
			String content = mapMessage.getString("content");
			String email = mapMessage.getString("email");
			MailUtils.sendMail(subject, content, email);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
