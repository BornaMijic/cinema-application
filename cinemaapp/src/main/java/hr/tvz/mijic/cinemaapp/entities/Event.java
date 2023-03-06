package hr.tvz.mijic.cinemaapp.entities;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "start_date")
    private LocalDateTime date;

    @Column(name = "price")
    private Double price;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_hall_id", insertable = false, updatable = false)
    @ToString.Exclude
    private CinemaHall cinemaHall;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Movie movie;

    @OneToMany(mappedBy = "eventReservation", fetch = FetchType.LAZY)
    @ToString.Exclude
    List<ReservationSeat> reservationSeatList;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, orphanRemoval=true)
    @ToString.Exclude
    List<Reservation> reservation;

    @Column(name = "cinema_hall_id")
    private Long idCinemaHall;

    @Column(nullable = false, name = "movie_id")
    private Long idMovie;

    public Event() {
    }

    public Event(Long id, LocalDateTime date,  Double price, Long idMovie, Long idCinemaHall) {
        this.id = id;
        this.date = date;
        this.price = price;
        this.idMovie = idMovie;
        this.idCinemaHall = idCinemaHall;
    }

    public Event(LocalDateTime date, Double price, Long idMovie, Long idCinemaHall) {
        this.date = date;
        this.price = price;
        this.idMovie = idMovie;
        this.idCinemaHall = idCinemaHall;
    }

}
