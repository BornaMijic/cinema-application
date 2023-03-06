package hr.tvz.mijic.cinemaapp.entities;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;


@Entity
@Table(name = "reservationSeats")
@Data
public class ReservationSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", insertable=false, updatable=false)
    @ToString.Exclude
    private Seat seat;


    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="reservation_id", updatable = false, insertable = false)
    @ToString.Exclude
    private Reservation reservation;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", insertable = false, updatable = false, nullable=false)
    @ToString.Exclude
    private Event eventReservation;

    @Column(name = "seat_id")
    private Long seatId;

    @Column(name = "event_id")
    private Long eventId;

    public ReservationSeat() {
    }

    public ReservationSeat(Long seatId, Long eventId) {
        this.seatId = seatId;
        this.eventId = eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservationSeat )) return false;
        return id != null && id.equals(((ReservationSeat) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}
