package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

@Data
public class MovieAndroidDTO {
    private Long id;
    private String title;
    private String image;

    public MovieAndroidDTO(Long id, String title, String image) {
        this.id = id;
        this.title = title;
        this.image = image;
    }
}
