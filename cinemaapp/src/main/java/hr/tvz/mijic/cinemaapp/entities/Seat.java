package hr.tvz.mijic.cinemaapp.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "seats")
@Data
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_number")
    private Integer seatNumber;

    @Column(name = "hall_row_number")
    private int rowNumber;

    @Column(name = "grid_position")
    private int gridPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    private CinemaHall cinemaHall;

    @OneToMany(mappedBy = "seat", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ReservationSeat> reservationSeats;

    public Seat() {
    }

    public Seat(Integer seatNumber, int rowNumber, int gridPosition) {
        this.seatNumber = seatNumber;
        this.rowNumber = rowNumber;
        this.gridPosition = gridPosition;
    }
}
