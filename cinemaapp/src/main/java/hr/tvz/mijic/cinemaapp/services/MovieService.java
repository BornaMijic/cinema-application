package hr.tvz.mijic.cinemaapp.services;

import hr.tvz.mijic.cinemaapp.DTOs.*;
import hr.tvz.mijic.cinemaapp.commands.MovieCommand;
import hr.tvz.mijic.cinemaapp.commands.MovieUpdateCommand;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface MovieService {
    List<MovieAndroidDTO> getActiveMovies();

    List<MovieDTO> getLatestMovies();

    List<MovieDTO> getSoonStartingMovies();

    PageMovieDTO findFiveMoviesPerPage(int pageNumber);

    PageMovieDTO findFiveMoviesPerPageWhichContainsSearchText(int pageNumber, String searchText);

    PageMovieAdminDTO findFiveMoviesPerPageAdmin(int pageNumber);

    PageMovieAdminDTO findFiveMoviesPerPageWhichContainsSearchTextAdmin(int pageNumber, String searchText);

    MovieAndImageNameDTO getMovieById(Long id);

    MovieAndImageNameDTO getMovieByIdAdmin(Long id);

    HashMap<String, Object> addMovie(MovieCommand movieCommand, MultipartFile image);

    boolean updateMovie(MovieUpdateCommand movieUpdateCommand, String oldImageName, MultipartFile image);

    void deleteMovie(Long id);

    void deleteMovieAndImage(Long id, String imageName) throws IOException;
}
