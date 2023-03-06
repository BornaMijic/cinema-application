package hr.tvz.mijic.cinemaapp.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "posters")
@Data
public class Poster {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "poster_type")
    private String imageType;

    @Column(name = "poster_name")
    private String imageName;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Movie movie;

    public Poster() {

    }

    public Poster(String imageType, String imageName) {
        this.imageType = imageType;
        this.imageName = imageName;
    }


}
