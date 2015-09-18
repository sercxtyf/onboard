package com.onboard.service.email.smtp;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.onboard.service.email.InternalEmailService;

/**
 * {@link InternalEmailService} fmtp邮件服务
 * 
 * @author xingliang
 * 
 */
@Service("emailServiceBean")
public class FmtpEmailServiceImpl implements InternalEmailService {

    public static final Logger logger = LoggerFactory.getLogger(FmtpEmailServiceImpl.class);

    @Override
    @Async
    public Future<Boolean> sendEmail(String to, String[] cc, String[] bcc, String subject, String content, String replyTo) {
        return this.sendEmail(null, to, cc, bcc, subject, content, replyTo);
    }

    @Override
    @Async
    public Future<Boolean> sendEmail(String from, String to, String[] cc, String[] bcc, String subject, String content,
            String replyTo) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("");
        mailSender.setPort(25);
        mailSender.setUsername("");
        mailSender.setPassword("");
        boolean result = false;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setCc(cc);
        message.setBcc(bcc);
        message.setText(content);
        message.setSubject(subject);
        message.setReplyTo(replyTo);
        try {
            mailSender.send(message);
            result = true;
        } catch (Exception e) {
            result = false;
        }

        return new AsyncResult<Boolean>(result);
    }

}
