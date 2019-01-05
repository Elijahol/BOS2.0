package cn.itcast.bos.web.action;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.constant.Constants;
import cn.itcast.bos.utils.MailUtils;
import cn.itcast.crm.domain.Customer;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class CustomerAction extends BaseAction<Customer> {
	@Autowired
	@Qualifier("jmsQueueTemplate")
	private JmsTemplate jmsTemplate;

	@Action(value = "customer_sendSms")
	public String sendSms() {
		// 手机号保存在Customer对象里面
		// 生成短信验证码
		String randomCode = RandomStringUtils.randomNumeric(4);
		// 将短信验证码保存到session
		ServletActionContext.getRequest().getSession()
				.setAttribute(model.getTelephone(), randomCode);
		final String msg = "尊敬的用户您好，本次获取的验证码为：" + randomCode
				+ ",服务电话：4006184000";

		// 调用MQ服务，发送一条信息
		jmsTemplate.send("bos_sms", new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("telephone", model.getTelephone());
				mapMessage.setString("msg", msg);
				return mapMessage;
			}
		});
		return NONE;

	}

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	// 属性驱动
	private String checkCode;

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	@Action(value = "customer_regist", results = {
			@Result(name = "success", type = "redirect", location = "signup-success.html"),
			@Result(name = "input", type = "redirect", location = "signup.html") })
	public String regist() {
		// 校验验证码
		// 从Session中获取之前生成验证码
		String checkCodeSession = (String) ServletActionContext.getRequest()
				.getSession().getAttribute(model.getTelephone());
		if (checkCodeSession == null || !checkCodeSession.equals(checkCode)) {
			return INPUT;
		}
		// 调用WebService连接CRM 保存客户信息
		WebClient
				.create(Constants.CRM_MANAGEMENT_URL
						+ "/services/customerService/customer")
				.type(MediaType.APPLICATION_JSON).post(model);
		System.out.println("客户注册成功...");

		String activeCode = RandomStringUtils.randomNumeric(32);
		final String content = "尊敬的用户您好，请于24小时内，进行邮箱账户的绑定，点击下面地址完成绑定：</br><a href='"
				+ MailUtils.activeUrl
				+ "?telephone="
				+ model.getTelephone()
				+ "&activeCode=" + activeCode + "'>速运快递的邮箱绑定地址</a>";
		// 调用activeMQ发送一封邮箱绑定的邮件
		jmsTemplate.send("bos_mail", new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("subject", "速运快递激活邮件");
				mapMessage.setString("content", content);
				mapMessage.setString("email", model.getEmail());
				return mapMessage;
			}
		});
		// MailUtils.sendMail("速运快递激活邮件", content, model.getEmail());
		// 将激活码保存到redis，设置24小时失效
		redisTemplate.opsForValue().set(model.getTelephone(), activeCode, 24,
				TimeUnit.HOURS);
		return SUCCESS;
	}

	private String activeCode;

	public void setActiveCode(String activeCode) {
		this.activeCode = activeCode;
	}

	@Action("customer_activeMail")
	public String activeMail() throws IOException {
		ServletActionContext.getResponse().setContentType(
				"text/html;charset=utf-8");
		// 判断激活码是否有效
		String activeCodeRedis = redisTemplate.opsForValue().get(
				model.getTelephone());
		if (activeCode == null || !activeCode.equals(activeCodeRedis)) {
			// 无效
			ServletActionContext.getResponse().getWriter()
					.println("激活码无效，请登录系统，重新绑定邮箱");
		} else {
			// 有效
			// 防止重复绑定
			// 调用CRM webservice 查询客户信息，判断是否已经绑定
			Customer customer = WebClient
					.create(Constants.CRM_MANAGEMENT_URL
							+ "/services/customerService/customer/telephone/"
							+ model.getTelephone())
					.accept(MediaType.APPLICATION_JSON).get(Customer.class);
			if (customer.getType() == null || customer.getType() != 1) {
				// 没有绑定
				WebClient
						.create(Constants.CRM_MANAGEMENT_URL
								+ "/services/customerService/customer/updatetype/"
								+ model.getTelephone()).get();
				ServletActionContext.getResponse().getWriter()
						.println("邮箱绑定成功！");
			} else {
				ServletActionContext.getResponse().getWriter()
						.println("邮箱已经绑定，无需重复绑定！");
			}
			// 删除激活码
			redisTemplate.delete(model.getTelephone());

		}
		return NONE;
	}

	@Action(value = "customer_login", results = {
			@Result(name = "login", location = "login.html", type = "redirect"),
			@Result(name = "success", location = "index.html#/myhome", type = "redirect") })
	public String login() {
		Customer customer = WebClient
				.create(Constants.CRM_MANAGEMENT_URL
						+ "/services/customerService/customer/login?telephone="
						+ model.getTelephone() + "&password="
						+ model.getPassword())
				.accept(MediaType.APPLICATION_JSON).get(Customer.class);
		if (customer == null) {
			return LOGIN;
		}else{
			ServletActionContext.getRequest().getSession().setAttribute("customer", customer);
			return SUCCESS;
		}
		
	}
}
