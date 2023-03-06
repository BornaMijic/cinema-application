package hr.tvz.mijic.cinemaapp.services;

import hr.tvz.mijic.cinemaapp.DTOs.PdfDTO;
import hr.tvz.mijic.cinemaapp.commands.PdfCommand;

import java.io.IOException;

public interface PdfService {
    PdfDTO generatePdf(PdfCommand tickets, Long idUser) throws IOException;

    PdfDTO generatePdfAdmin(PdfCommand tickets) throws IOException;

}
