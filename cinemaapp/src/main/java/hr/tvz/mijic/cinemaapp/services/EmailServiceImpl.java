package hr.tvz.mijic.cinemaapp.services;


import com.lowagie.text.*;
import hr.tvz.mijic.cinemaapp.commands.EmailCommand;
import hr.tvz.mijic.cinemaapp.commands.PdfCommand;
import hr.tvz.mijic.cinemaapp.repositories.EmailSender;
import hr.tvz.mijic.cinemaapp.repositories.PdfGeneration;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;


@Component
public class EmailServiceImpl implements EmailService {

    private EmailSender emailSender;
    private PdfGeneration pdfGeneration;


    public EmailServiceImpl(EmailSender emailSender, PdfGeneration pdfGeneration) {
        this.emailSender = emailSender;
        this.pdfGeneration = pdfGeneration;
    }

    @Override
    public void sendTicketsOnEmail(EmailCommand tickets) throws DocumentException, IOException, MessagingException {
        PdfCommand pdfTickets = new PdfCommand(tickets.getCinemaHallName(), tickets.getTitle(), tickets.getDateString(), tickets.getHourString(), tickets.getPrice(), tickets.getUserReservationSeats());
        byte[] bytes = pdfGeneration.generatePdf(pdfTickets);
        emailSender.sendMessageWithAttachment(tickets.getUserEmail(), tickets.getTitle(), bytes);

    }

    @Override
    public void sendMessageForVerifyingEmail(String email, String username, String link, String codeToVerify) throws MessagingException {
        emailSender.sendMessageForVerifyingEmail(email, username, link, codeToVerify);
    }

}
