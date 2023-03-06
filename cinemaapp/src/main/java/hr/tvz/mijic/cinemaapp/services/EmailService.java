package hr.tvz.mijic.cinemaapp.services;

import com.google.zxing.WriterException;
import com.lowagie.text.DocumentException;
import hr.tvz.mijic.cinemaapp.commands.EmailCommand;

import javax.mail.MessagingException;
import java.io.IOException;

public interface EmailService {
    void sendTicketsOnEmail(EmailCommand tickets) throws DocumentException, IOException, WriterException, MessagingException;

    void sendMessageForVerifyingEmail(String email, String username, String link, String codeToVerify) throws MessagingException;
}
