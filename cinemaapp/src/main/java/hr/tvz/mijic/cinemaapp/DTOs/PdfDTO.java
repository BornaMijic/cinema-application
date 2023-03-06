package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

@Data
public class PdfDTO {
    private String pdfString;

    public PdfDTO(String pdfString) {
        this.pdfString = pdfString;
    }
}
