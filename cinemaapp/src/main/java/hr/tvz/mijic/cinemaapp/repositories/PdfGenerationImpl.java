package hr.tvz.mijic.cinemaapp.repositories;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.lowagie.text.*;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import hr.tvz.mijic.cinemaapp.commands.PdfCommand;
import hr.tvz.mijic.cinemaapp.commands.UserReservationCommand;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class PdfGenerationImpl implements PdfGeneration {

    @Override
    public byte[] generatePdf(PdfCommand tickets) throws IOException {
        Document ticketDocument = new Document(PageSize.A4);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter ticketWriter = PdfWriter.getInstance(ticketDocument, outputStream);
        PdfContentByte pdfContentByte = new PdfContentByte(ticketWriter);
        ticketDocument.open();
        int i = 0;
        for (UserReservationCommand ticket : tickets.getUserReservationSeats()) {
            PdfPTable ticketTable = new PdfPTable(5);
            ticketTable.setSpacingAfter(20f);
            Font defaultSize = new Font(Font.COURIER, 10, Font.NORMAL);
            PdfPCell emptyCell = new PdfPCell();
            emptyCell.setBorder(Rectangle.RIGHT);
            PdfPCell topCell = new PdfPCell();
            topCell.setFixedHeight(30f);
            topCell.setColspan(5);
            topCell.setBorder(Rectangle.TOP);
            topCell.setBorder(Rectangle.RIGHT);
            topCell.setBorder(13);
            topCell.setBackgroundColor(new Color(206, 90, 90));
            ticketTable.addCell(topCell);
            PdfPCell redCell = new PdfPCell();
            redCell.setRowspan(5);
            redCell.setBorder(6);
            redCell.setBackgroundColor(new Color(169, 67, 67));
            ticketTable.addCell(redCell);
            PdfPCell seatNumberCell = new PdfPCell();
            Paragraph seatNumber = new Paragraph("Sjedalo: " + ticket.getSeatNumber(), defaultSize);
            seatNumberCell.setBorder(Rectangle.NO_BORDER);
            seatNumberCell.addElement(seatNumber);
            PdfPCell rowNumberCell = new PdfPCell();
            Paragraph rowNumber = new Paragraph("Red: " + ticket.getRowNumber(), defaultSize);
            rowNumberCell.setBorder(Rectangle.NO_BORDER);
            rowNumberCell.addElement(rowNumber);
            PdfPCell movieTitleCell = new PdfPCell();
            Paragraph movieTitle = new Paragraph(tickets.getTitle(), defaultSize);
            movieTitleCell.setBorder(Rectangle.NO_BORDER);
            movieTitleCell.setColspan(2);
            movieTitleCell.addElement(movieTitle);
            PdfPCell priceCell = new PdfPCell();
            Paragraph price = new Paragraph("Cijena: " + String.format("%.02f", tickets.getPrice()) + "€", defaultSize);
            priceCell.setBorder(Rectangle.NO_BORDER);
            priceCell.addElement(price);
            PdfPCell cinemaHallCell = new PdfPCell();
            Paragraph cinemaHall = new Paragraph("Kino dvorana: " + tickets.getCinemaHallName(), defaultSize);
            cinemaHallCell.setBorder(Rectangle.NO_BORDER);
            cinemaHallCell.addElement(cinemaHall);
            PdfPCell startDateCell = new PdfPCell();

            Paragraph startDate = new Paragraph(tickets.getDateString(), defaultSize);
            startDateCell.setBorder(Rectangle.NO_BORDER);
            startDateCell.addElement(startDate);
            startDateCell.setColspan(2);

            PdfPCell hoursCell = new PdfPCell();
            Paragraph hours = new Paragraph("Vrijeme pocetka:" + tickets.getHourString(), defaultSize);
            hoursCell.setBorder(Rectangle.NO_BORDER);
            hoursCell.addElement(hours);
            ticketTable.addCell(movieTitleCell);
            ticketTable.addCell(priceCell);
            ticketTable.addCell(emptyCell);

            ticketTable.addCell(cinemaHallCell);
            ticketTable.addCell(rowNumberCell);
            ticketTable.addCell(seatNumberCell);
            ticketTable.addCell(emptyCell);

            ticketTable.addCell(startDateCell);
            ticketTable.addCell(hoursCell);
            ticketTable.addCell(emptyCell);

            PdfPCell qrCodeCell = new PdfPCell();
            qrCodeCell.setColspan(4);
            qrCodeCell.setBorder(10);
            qrCodeCell.setFixedHeight(50f);
            com.lowagie.text.Image qrCodeJpg = generateQrCode(pdfContentByte, ticket);
            qrCodeCell.addElement(qrCodeJpg);
            qrCodeCell.setPadding(5);
            i++;


            ticketTable.addCell(qrCodeCell);

            if (i % 2 == 0) {
                ticketTable.setSpacingAfter(PageSize.A4.getHeight());
            }
            ticketDocument.add(ticketTable);
        }
        ticketDocument.close();

        return outputStream.toByteArray();
    }

    private com.lowagie.text.Image generateQrCode(PdfContentByte pdfContentByte, UserReservationCommand ticket) throws IOException {
        Code128Writer code128Writer = new Code128Writer();
        BitMatrix qrCodeEncoded = code128Writer.encode(ticket.getIdReservationSeat().toString(), BarcodeFormat.CODE_128, 600, 150);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(qrCodeEncoded);
        return Image.getInstance(pdfContentByte, bufferedImage, 1);
    }
}
