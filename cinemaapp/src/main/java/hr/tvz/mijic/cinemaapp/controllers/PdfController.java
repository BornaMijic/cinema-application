package hr.tvz.mijic.cinemaapp.controllers;

import com.google.zxing.WriterException;
import hr.tvz.mijic.cinemaapp.DTOs.PdfDTO;
import hr.tvz.mijic.cinemaapp.commands.PdfCommand;
import hr.tvz.mijic.cinemaapp.services.PdfService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/pdf")
@CrossOrigin(origins = "http://localhost:4200")
public class PdfController {

    private PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping(value = "/export/{idUser}")
    public ResponseEntity<PdfDTO> downloadTicketsPdf(@RequestBody PdfCommand tickets, @PathVariable Long idUser) throws WriterException, IOException {
        PdfDTO pdfDTO = pdfService.generatePdf(tickets, idUser);
        return new ResponseEntity<PdfDTO>(pdfDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/admin/export")
    public ResponseEntity<PdfDTO> downloadTicketsPdf(@RequestBody PdfCommand tickets) throws WriterException, IOException {
        PdfDTO pdfDTO = pdfService.generatePdfAdmin(tickets);
        return new ResponseEntity<PdfDTO>(pdfDTO, HttpStatus.OK);
    }
}
