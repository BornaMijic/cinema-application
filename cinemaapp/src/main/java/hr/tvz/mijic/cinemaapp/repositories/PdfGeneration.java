package hr.tvz.mijic.cinemaapp.repositories;

import hr.tvz.mijic.cinemaapp.commands.PdfCommand;

import java.io.IOException;

public interface PdfGeneration {
    byte[] generatePdf(PdfCommand tickets) throws IOException;
}
