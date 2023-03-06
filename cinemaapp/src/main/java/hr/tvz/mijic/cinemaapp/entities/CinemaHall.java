package hr.tvz.mijic.cinemaapp.entities;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "cinema_halls")
@Data
public class CinemaHall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "hall_name")
    private String name;

    @Column(name = "grid_rows_number")
    private int gridRowsNumber;

    @Column(name = "grid_columns_number")
    private int gridColumnsNumber;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name = "cinema_hall_id", updatable = false, insertable = false)
    @ToString.Exclude
    private List<Seat> seats;

    @OneToMany(mappedBy = "cinemaHall", fetch = FetchType.LAZY, orphanRemoval=true)
    @ToString.Exclude
    List<Event> event;

    public CinemaHall() {
    }

    public CinemaHall(String name, int gridRowsNumber, int gridColumnsNumber) {
        this.name = name;
        this.gridRowsNumber = gridRowsNumber;
        this.gridColumnsNumber = gridColumnsNumber;
    }
}
