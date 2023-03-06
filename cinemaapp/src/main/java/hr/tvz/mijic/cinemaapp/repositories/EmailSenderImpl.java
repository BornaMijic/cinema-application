package hr.tvz.mijic.cinemaapp.repositories;

import com.lowagie.text.DocumentException;
import com.sun.istack.ByteArrayDataSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
public class EmailSenderImpl implements EmailSender {

    private final JavaMailSender gmailSender;

    public EmailSenderImpl() {
        this.gmailSender = getJavaMailSender();
    }

    @Override
    public void sendMessageWithAttachment(String email, String title, byte[] bytes) throws MessagingException, DocumentException {
        MimeMessage sendingMessage = gmailSender.createMimeMessage();

        MimeMessageHelper messageSpecifics = new MimeMessageHelper(sendingMessage, true);

        messageSpecifics.setTo(email);
        messageSpecifics.setSubject("Ulaznice");
        messageSpecifics.setText("Hvala što ste kupili ulaznice za " + title);

        messageSpecifics.addAttachment("tickets.pdf", new ByteArrayDataSource(bytes, "application/pdf"));

        gmailSender.send(sendingMessage);
    }

    @Override
    public void sendMessageForVerifyingEmail(String email, String username,
                                             String link, String codeToVerifyEmail)
            throws MessagingException {
        MimeMessage sendingMessage = gmailSender.createMimeMessage();

        MimeMessageHelper messageSpecifics = new MimeMessageHelper(sendingMessage,
                true);

        messageSpecifics.setTo(email);
        messageSpecifics.setSubject("Potvrdite svoj gmail");
        messageSpecifics.setText("",
                "<p> Bok, " + username + " </p>" +
                        "<p> Da potvrdite svoj gmail kliknite na navedeni link  <br/>"
                        + link + "/verify-code/" + username + "/" + codeToVerifyEmail + "</p>");

        gmailSender.send(sendingMessage);
    }

    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl gmailSender = new JavaMailSenderImpl();

        Properties properties = gmailSender.getJavaMailProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.username", "kinoapp2023@gmail.com");
        properties.put("mail.password", "fydmiocuiwswshpb");
        properties.put("mail.smtp.starttls.required", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session gmailSession = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        "kinoapp2023@gmail.com", "fydmiocuiwswshpb");
            }
        });

        gmailSender.setSession(gmailSession);

        return gmailSender;
    }

}

