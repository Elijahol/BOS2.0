package cn.itcast.bos.mq;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.springframework.stereotype.Service;

@Service("smsConsumer")
public class SmsConsumer implements MessageListener{

	@Override
	public void onMessage(Message message) {
		MapMessage mapMessage = (MapMessage) message;
		//调用SMS服务发送短信
		/**
		 * 采用第三方短信平台发送短信 此处略过
		 */
		try{
		String result = "000/xxx";
		if (result.startsWith("000")) {
			//发送成功
			System.out.println("发送短信成功，手机号：" 
					+ mapMessage.getString("telephone")+", 验证码： "
					+mapMessage.getString("msg"));
		} else {
			throw new RuntimeException("短信发送失败，信息码：" + result);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
