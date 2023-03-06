package hr.tvz.mijic.cinemaapp.entities;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "movies")
@Data
public class Movie {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "poster_name")
    private String posterName;

    @Column(name = "summary")
    private String summary;

    @Column(name = "duration")
    private int duration;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    @ToString.Exclude
    List<Event> events;

    public Movie() {
    }

    public Movie(String title, String posterName, String summary, int duration) {
        this.title = title;
        this.posterName = posterName;
        this.summary = summary;
        this.duration = duration;
    }

    public Movie(Long id, String title, String posterName, String summary, int duration) {
        this.id = id;
        this.title = title;
        this.posterName = posterName;
        this.summary = summary;
        this.duration = duration;
    }
}
