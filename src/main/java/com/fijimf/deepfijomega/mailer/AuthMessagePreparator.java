package com.fijimf.deepfijomega.mailer;

import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class AuthMessagePreparator implements MimeMessagePreparator {

    private final String email;
    private final String authCode;

    public AuthMessagePreparator(String username, String email, String authCode) {
        this.email = email;
        this.authCode = authCode;
    }

    public void prepare(MimeMessage mimeMessage) throws Exception {

        mimeMessage.setRecipient(Message.RecipientType.TO,
                new InternetAddress(email));
        mimeMessage.setFrom(new InternetAddress("deepfij@gmail.com"));
        mimeMessage.setText("");
    }
}

