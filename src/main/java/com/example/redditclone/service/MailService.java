package com.example.redditclone.service;

import com.example.redditclone.exceptions.SpringRedditCloneException;
import com.example.redditclone.model.NotificationEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailContentBuilderService mailContentBuilderService;

    void sendMail(NotificationEmail notificationEmail) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom("soelinnthant000@gmail.com");
            mimeMessageHelper.setTo(notificationEmail.getRecipient());
            mimeMessageHelper.setSubject(notificationEmail.getSubject());
            mimeMessageHelper.setText(notificationEmail.getBody());
        };

        try {
            mailSender.send(messagePreparator);
            log.info("Activation mail sent successfully!");
        } catch (MailException e) {
            throw new SpringRedditCloneException("Cannot send activation mail to " + notificationEmail.getRecipient());
        }
    }

}
