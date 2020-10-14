package com.fijimf.deepfijomega.mailer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Component
public class Mailer {
    @Autowired
    private JavaMailSender javaMailSender;

    @PostConstruct
    public void sendEmail() {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("fijimf@gmail.com");

        msg.setSubject("DEEPFIJ Ω STARTED");
        msg.setText("Deep Fij Omega was started at " + LocalDateTime.now());

        javaMailSender.send(msg);
    }

    public void sendAuthEmail(String username, String email, String authCode) {
       javaMailSender.send(new AuthMessagePreparator(username,email, authCode));
    }
}