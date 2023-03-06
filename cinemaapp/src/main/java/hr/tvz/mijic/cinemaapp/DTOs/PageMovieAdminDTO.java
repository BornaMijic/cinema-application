package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class PageMovieAdminDTO {
    private Long count;
    private List<MovieAndImageNameDTO> moviesDTOList;

    public PageMovieAdminDTO() {
    }

    public PageMovieAdminDTO(Long count, List<MovieAndImageNameDTO> moviesDTOList) {
        this.count = count;
        this.moviesDTOList = moviesDTOList;
    }
}
