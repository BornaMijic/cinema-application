package hr.tvz.mijic.cinemaapp.controllers;


import com.google.zxing.WriterException;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import hr.tvz.mijic.cinemaapp.DTOs.ResponseOrder;
import hr.tvz.mijic.cinemaapp.commands.CaptureRequestCommand;
import hr.tvz.mijic.cinemaapp.commands.RequestAmount;
import hr.tvz.mijic.cinemaapp.services.EmailService;
import hr.tvz.mijic.cinemaapp.services.ReservationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    @Value("${paypal.clientId}")
    private String clientId;

    @Value("${paypal.secret}")
    private String secret;

    private ReservationService reservationService;

    private EmailService emailService;

    public PaymentController(ReservationService reservationService, EmailService emailService) {
        this.reservationService = reservationService;
        this.emailService = emailService;
    }

    @PostMapping("order")
    public ResponseEntity<ResponseOrder> createOrder(@RequestBody RequestAmount requestAmount) {

        PayPalHttpClient payPalHttpClient = createClient();
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");
        AmountWithBreakdown priceAndCurrency = new AmountWithBreakdown().currencyCode("EUR").value(requestAmount.getPrice().toString());
        PurchaseUnitRequest ticketsPriceSum = new PurchaseUnitRequest().amountWithBreakdown(priceAndCurrency);
        List<PurchaseUnitRequest> purchaseUnitRequestList = new ArrayList<>();
        purchaseUnitRequestList.add(ticketsPriceSum);
        orderRequest.purchaseUnits(purchaseUnitRequestList);
        OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest().requestBody(orderRequest);
        try {
            HttpResponse<Order> getInfoResponse = payPalHttpClient.execute(ordersCreateRequest);
            Order order = getInfoResponse.result();
            ResponseOrder responseOrder = new ResponseOrder(order.id());
            return new ResponseEntity<>(responseOrder, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("approve")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Order> approveOrder(@RequestBody CaptureRequestCommand captureRequestCommand){
        PayPalHttpClient payPalHttpClient = createClient();
        OrdersCaptureRequest ordersCaptureRequest= new OrdersCaptureRequest(captureRequestCommand.getIdOrder());
        try {
            boolean success = this.reservationService.confirmReservation(captureRequestCommand.getIdReservation());
            if(success == true) {
                HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersCaptureRequest);
                try {
                    if(Objects.equals(httpResponse.result().status(), "COMPLETED")) {
                        emailService.sendTicketsOnEmail(captureRequestCommand.getEmailInfo());

                        return new ResponseEntity<>(httpResponse.result(), HttpStatus.OK);

                    } else {
                        this.reservationService.deleteByIdAndUserId(captureRequestCommand.getIdReservation(), captureRequestCommand.getIdUser());
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                } catch (MessagingException e) {
                    return new ResponseEntity<>(httpResponse.result(), HttpStatus.OK);
                } catch (WriterException e) {
                    return new ResponseEntity<>(httpResponse.result(), HttpStatus.OK);
                } catch(Exception e) {
                    return new ResponseEntity<>(httpResponse.result(), HttpStatus.OK);
                }
            } else {
                this.reservationService.deleteByIdAndUserId(captureRequestCommand.getIdReservation(), captureRequestCommand.getIdUser());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            this.reservationService.deleteByIdAndUserId(captureRequestCommand.getIdReservation(), captureRequestCommand.getIdUser());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private PayPalHttpClient createClient() {
        PayPalEnvironment payPalEnvironment = new PayPalEnvironment.Sandbox(clientId, secret);
        PayPalHttpClient payPalHttpClient = new PayPalHttpClient(payPalEnvironment);
        return payPalHttpClient;
    }
}
