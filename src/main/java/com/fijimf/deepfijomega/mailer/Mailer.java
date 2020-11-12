package com.fijimf.deepfijomega.mailer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Locale;

@Component
public class Mailer {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;


    public void sendEmail() {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("fijimf@gmail.com");

        msg.setSubject("DEEPFIJ Ω STARTED");
        msg.setText("Deep Fij Omega was started at " + LocalDateTime.now());

        javaMailSender.send(msg);
    }

    public void sendStartupMessage(String password) throws MessagingException, IOException {
        // Prepare the evaluation context
        final Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("time", LocalDateTime.now());
        try {
            ctx.setVariable("hostname", InetAddress.getLocalHost().getHostName());
        } catch (Exception ex) {
            ctx.setVariable("hostname", "unknown");
        }
        ctx.setVariable("password", password);
        ctx.setVariable("imageResourceName", "deepfij.png"); // so that we can reference it from HTML
        byte[] imgBytes = ClassLoader.getSystemClassLoader().getResourceAsStream("static/img/deepfij.png").readAllBytes();
        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper message =
                new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
        message.setSubject("DeepFij-Ω started");
        message.setFrom("deepfij@gmail.com");
        message.setTo("fijimf@gmail.com");

        // Create the HTML body using Thymeleaf
        final String htmlContent = templateEngine.process("mail/server-started.html", ctx);
        message.setText(htmlContent, true); // true = isHtml

        // Add the inline image, referenced from the HTML code as "cid:${imageResourceName}"
        final InputStreamSource imageSource = new ByteArrayResource(imgBytes);
        message.addInline("deepfij.png", imageSource, "image/png");

        // Send mail
        javaMailSender.send(mimeMessage);
    }

    public void sendAuthEmail(String username, String email, String authCode) {
       javaMailSender.send(new AuthMessagePreparator(username,email, authCode));
    }
}