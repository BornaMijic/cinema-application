package hr.tvz.mijic.cinemaapp.repositories;

import com.lowagie.text.DocumentException;

import javax.mail.MessagingException;

public interface EmailSender {
    void sendMessageWithAttachment(String email, String title, byte[] bytes) throws MessagingException, DocumentException;

    void sendMessageForVerifyingEmail(String email, String username, String link, String codeToVerifyEmail) throws MessagingException;
}
