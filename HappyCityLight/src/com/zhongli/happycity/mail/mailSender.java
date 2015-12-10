package com.zhongli.happycity.mail;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class mailSender {
	public static void sendMail(String from_emial, String from_name,
			String to_email, String to_name, String title, String content)
			throws UnsupportedEncodingException, MessagingException {
		Properties props = new Properties();
		// Session session = Session.getDefaultInstance(props, null);
		final String username = "lzl19920403@163.com";
		final String password = "jcsss130";
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.163.com");
		props.put("mail.smtp.port", "25");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(from_emial, from_name));
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
				to_email, to_name));
		msg.setSubject(title);
		msg.setText(content);
		Transport.send(msg);

	}
}
