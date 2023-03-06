package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

@Data
public class MovieAndImageNameDTO {
    private Long id;
    private String title;
    private String imageName;
    private String imageType;
    private String image;
    private String summary;
    private int duration;

    public MovieAndImageNameDTO() {
    }

    public MovieAndImageNameDTO(Long id, String title, String imageName, String imageType, String image, String summary, int duration) {
        this.id = id;
        this.title = title;
        this.imageName = imageName;
        this.imageType = imageType;
        this.image = image;
        this.summary = summary;
        this.duration = duration;
    }
}
