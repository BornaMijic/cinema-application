package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

@Data
public class MovieDTO {
    private Long id;
    private String title;
    private String imageType;
    private String image;
    private String summary;
    private int duration;

    public MovieDTO() {
    }

    public MovieDTO(Long id, String title, String imageType, String image, String summary, int duration) {
        this.id = id;
        this.title = title;
        this.imageType = imageType;
        this.image = image;
        this.summary = summary;
        this.duration = duration;
    }
}
