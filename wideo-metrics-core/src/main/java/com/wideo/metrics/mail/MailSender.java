package com.wideo.metrics.mail;

import java.io.StringReader;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.*;

import javax.activation.*;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
@ComponentScan("com.wideo.metrics")
public class MailSender {

    private final Logger LOGGER = Logger.getLogger(MailSender.class);

    public MailSender() {

    }

    public void sendEmail(String toEmail, String fromEmail, String subject,
            String text) {

        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "email-smtp.us-east-1.amazonaws.com");
        props.put("mail.smtp.port", "587"); // 25, 465 or 587

        // TODO: Configure Username and Password for emails
        // SMTP Username: AKIAIXUNLLFQ7GMQJEUA
        // SMTP Password: AhP9RvqHQ6WUsetBNuTAhMiE88PZB3i6c9NGKjpCM0gt
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        "AKIAIXUNLLFQ7GMQJEUA",
                        "AhP9RvqHQ6WUsetBNuTAhMiE88PZB3i6c9NGKjpCM0gt");
            }
        });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(fromEmail));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                    toEmail));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
            message.setText(text);

            // Send message
            Transport.send(message);
            LOGGER.info("Email sent successfully to " + toEmail);
        }
        catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }
}
