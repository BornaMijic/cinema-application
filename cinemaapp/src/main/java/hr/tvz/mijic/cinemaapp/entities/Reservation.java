package hr.tvz.mijic.cinemaapp.entities;


import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservations")
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_time")
    private LocalDateTime reservationTime;

    @Column(name = "complete")
    private boolean complete;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @ToString.Exclude
    private User user;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Event event;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reservation_id", nullable=false)
    private List<ReservationSeat> reservationSeats = new ArrayList<>();

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "event_id")
    private Long eventId;

    public Reservation() {
    }

    public Reservation(Long id, LocalDateTime reservationTime, Long userId, Long eventId) {
        this.id = id;
        this.reservationTime = reservationTime;
        this.userId = userId;
        this.eventId = eventId;
    }

    public Reservation(LocalDateTime reservationTime, Long userId, Long eventId, boolean complete) {
        this.reservationTime = reservationTime;
        this.userId = userId;
        this.eventId = eventId;
        this.complete = complete;
    }

    public Reservation(LocalDateTime reservationTime, List<ReservationSeat> reservationSeats, Long userId, Long eventId) {
        this.reservationTime = reservationTime;
        this.reservationSeats = reservationSeats;
        this.userId = userId;
        this.eventId = eventId;
    }

    public void addReservationSeats(ReservationSeat reservationSeat) {
        reservationSeats.add(reservationSeat);
        reservationSeat.setReservation(this);
    }

    public void removeReservationSeat(ReservationSeat reservationSeat) {
        reservationSeat.setReservation(null);
        reservationSeats.remove(reservationSeat);
    }

}