package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class PageMovieDTO {
    private Long count;
    private List<MovieDTO> moviesDTOList;

    public PageMovieDTO() {
    }

    public PageMovieDTO(Long count, List<MovieDTO> moviesDTOList) {
        this.count = count;
        this.moviesDTOList = moviesDTOList;
    }

}
