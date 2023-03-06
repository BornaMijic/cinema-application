package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

@Data
public class ResponseOrder {
    private String orderId;

    public ResponseOrder(String orderId) {
        this.orderId = orderId;
    }
}
