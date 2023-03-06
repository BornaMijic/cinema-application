package hr.tvz.mijic.cinemaapp.controllers;

import com.google.zxing.WriterException;
import com.lowagie.text.DocumentException;
import hr.tvz.mijic.cinemaapp.commands.EmailCommand;
import hr.tvz.mijic.cinemaapp.commands.VerificationEmailInfoCommand;
import hr.tvz.mijic.cinemaapp.services.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("/mail")
@CrossOrigin(origins = "http://localhost:4200")
public class EmailController {

    public EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<EmailCommand> sendEmailWithAttachment(@Valid @RequestBody EmailCommand tickets) throws MessagingException, DocumentException, IOException, WriterException {
        emailService.sendTicketsOnEmail(tickets);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("verification-email")
    public ResponseEntity<EmailCommand> sendEmailWithUserVerificationLink(@Valid @RequestBody VerificationEmailInfoCommand verificationEmailInfoCommand) throws MessagingException {
        emailService.sendMessageForVerifyingEmail(verificationEmailInfoCommand.getEmail(), verificationEmailInfoCommand.getUsername(), verificationEmailInfoCommand.getLink(), verificationEmailInfoCommand.getCodeToVerifyEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}