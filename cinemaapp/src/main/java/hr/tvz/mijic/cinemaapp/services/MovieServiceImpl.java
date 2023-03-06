package hr.tvz.mijic.cinemaapp.services;

import hr.tvz.mijic.cinemaapp.DTOs.*;
import hr.tvz.mijic.cinemaapp.commands.MovieCommand;
import hr.tvz.mijic.cinemaapp.commands.MovieUpdateCommand;
import hr.tvz.mijic.cinemaapp.entities.Movie;
import hr.tvz.mijic.cinemaapp.repositories.MovieRepository;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public List<MovieAndroidDTO> getActiveMovies() {
        return movieRepository.getActiveMovies().stream().map(movie -> getMovieDTOAndroid(movie)).collect(Collectors.toList());
    }

    @Override
    public List<MovieDTO> getLatestMovies() {
        return movieRepository.getLatestMovies().stream().map(movie -> getMovieDTO(movie)).collect(Collectors.toList());
    }

    @Override
    public List<MovieDTO> getSoonStartingMovies() {
        return movieRepository.getSoonStartingMovies().stream().map(movie -> getMovieDTO(movie)).collect(Collectors.toList());
    }

    @Override
    public PageMovieDTO findFiveMoviesPerPage(int pageNumber) {
        Pageable requestingPageWithFiveMovies = PageRequest.of(pageNumber, 5);
        Page<Movie> moviesPage = movieRepository.findFiveMoviesPerPage(requestingPageWithFiveMovies);
        List<MovieDTO> moviesDTOList = moviesPage.stream().map(movie -> getMovieDTO(movie)).collect(Collectors.toList());
        return new PageMovieDTO(moviesPage.getTotalElements(), moviesDTOList);
    }

    @Override
    public PageMovieDTO findFiveMoviesPerPageWhichContainsSearchText(int pageNumber, String searchText) {
        Pageable pageWithFiveMovies = PageRequest.of(pageNumber, 5);
        Page<Movie> moviesPage = movieRepository.findFiveMoviesPerPageWhichContainsSearchText(searchText, pageWithFiveMovies);
        List<MovieDTO> moviesDTOList = moviesPage.stream().map(movie -> getMovieDTO(movie)).collect(Collectors.toList());
        return new PageMovieDTO(moviesPage.getTotalElements(), moviesDTOList);
    }

    @Override
    public PageMovieAdminDTO findFiveMoviesPerPageAdmin(int pageNumber) {
        Pageable requestingPageWithFiveMovies = PageRequest.of(pageNumber, 5);
        Page<Movie> moviesPage = movieRepository.findFiveMoviesPerPageAdmin(requestingPageWithFiveMovies);
        List<MovieAndImageNameDTO> moviesAdminDTOS = moviesPage.stream().map(movie -> getMovieAndImageNameDTO(movie)).collect(Collectors.toList());
        return new PageMovieAdminDTO(moviesPage.getTotalElements(), moviesAdminDTOS);
    }

    @Override
    public PageMovieAdminDTO findFiveMoviesPerPageWhichContainsSearchTextAdmin(int pageNumber, String searchText) {
        Pageable pageWithFiveMovies = PageRequest.of(pageNumber, 5);
        Page<Movie> moviesPage = movieRepository.findFiveMoviesPerPageWhichContainsSearchTextAdmin(searchText, pageWithFiveMovies);
        List<MovieAndImageNameDTO> moviesAdminDTOS = moviesPage.stream().map(movie -> getMovieAndImageNameDTO(movie)).collect(Collectors.toList());
        return new PageMovieAdminDTO(moviesPage.getTotalElements(), moviesAdminDTOS);
    }

    @Override
    public MovieAndImageNameDTO getMovieById(Long id) {
        Movie movie = movieRepository.findMovieByIdWhichStartsInNext30days(id);
        if (movie != null) {
            return getMovieAndImageNameDTO(movie);
        }
        return null;
    }

    @Override
    public MovieAndImageNameDTO getMovieByIdAdmin(Long id) {
        Optional<Movie> movieJpaOptional = movieRepository.findById(id);
        if (movieJpaOptional.isPresent()) {
            Movie movie = movieJpaOptional.get();
            return getMovieAndImageNameDTO(movie);
        }
        return null;
    }

    @Override
    @Transactional
    public HashMap<String, Object> addMovie(MovieCommand movieCommand, MultipartFile image) {
        try {
            Movie movie = movieRepository.save(new Movie(movieCommand.getTitle(), image.getOriginalFilename(), movieCommand.getSummary(), movieCommand.getDuration()));
            File directory = new File("src/main/resources/movies/");
            if (!directory.exists()) {
                Files.createDirectories(Paths.get("src/main/resources/movies/"));
            }
            Path path = Paths.get("src/main/resources/movies/" + movie.getId() + "-" + image.getOriginalFilename());
            Files.write(path, image.getBytes());
            HashMap<String, Object> movieHashMap = new HashMap<>();
            movieHashMap.put("idMovie", movie.getId());
            return movieHashMap;
        } catch (IOException ioException) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return null;
        } catch (Exception e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    @Override
    @Transactional
    public boolean updateMovie(MovieUpdateCommand movieUpdateCommand, String oldImageName, MultipartFile image) {
        File imageFile = null;
        try {
            if (image != null) {
                Movie movie = movieRepository.save(new Movie(movieUpdateCommand.getId(), movieUpdateCommand.getTitle(), image.getOriginalFilename(), movieUpdateCommand.getSummary(), movieUpdateCommand.getDuration()));
                File directory = new File("movies/");
                if (!directory.exists()) {
                    Files.createDirectories(Paths.get("src/main/resources/movies/"));
                }
                Path path = Paths.get("src/main/resources/movies/" + movie.getId() + "-" + image.getOriginalFilename());
                Files.write(path, image.getBytes());
                imageFile = new File("src/main/resources/movies/" + oldImageName);
                if(oldImageName.equals( movie.getId() + "-" + image.getOriginalFilename())) {
                    return true;
                }
            } else {
                movieRepository.save(new Movie(movieUpdateCommand.getId(), movieUpdateCommand.getTitle(), oldImageName, movieUpdateCommand.getSummary(), movieUpdateCommand.getDuration()));
                return true;
            }
        } catch (IOException ioException) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        } catch (Exception e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        try {
            FileUtils.forceDelete(imageFile);
            return true;
        } catch (IOException ioException) {
            return true;
        }
    }

    @Override
    @Transactional
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteMovieAndImage(Long id, String imageName) {
        movieRepository.deleteById(id);
        try {
            File imageFile = new File("src/main/resources/movies/" + id + "-" + imageName);
            FileUtils.forceDelete(imageFile);
        } catch (IOException ioException) {
            return;
        }

    }


    private MovieDTO getMovieDTO(Movie movie) {
        try {
            String posterName = movie.getPosterName();
            if (posterName.length() >= 4) {
                String fileType = posterName.substring(posterName.length() - 4) == "jpg" ? "jpeg" : "png";
                InputStream inputImage = new FileInputStream("src/main/resources/movies/" + movie.getId() + "-" + posterName);
                byte[] image = inputImage.readAllBytes();
                inputImage.close();
                return new MovieDTO(movie.getId(), movie.getTitle(), fileType, Base64.getEncoder().encodeToString(image), movie.getSummary(), movie.getDuration());
            } else {
                return new MovieDTO(movie.getId(), movie.getTitle(), "", "", movie.getSummary(), movie.getDuration());
            }

        } catch (IOException ioException) {
            return new MovieDTO(movie.getId(), movie.getTitle(), "", "", movie.getSummary(), movie.getDuration());
        }
    }

    private MovieAndroidDTO getMovieDTOAndroid(Movie movie) {
        return new MovieAndroidDTO(movie.getId(), movie.getTitle(), "");
    }

    private MovieAndImageNameDTO getMovieAndImageNameDTO(Movie movie) {
        try {
            String posterName = movie.getPosterName();
            if (posterName.length() >= 4) {
                String fileType = posterName.endsWith("jpg") ? "jpeg" : "png";
                InputStream inputImage = new FileInputStream("src/main/resources/movies/" + movie.getId() + "-" + posterName);
                byte[] image = inputImage.readAllBytes();
                inputImage.close();
                return new MovieAndImageNameDTO(movie.getId(), movie.getTitle(), posterName, fileType, Base64.getEncoder().encodeToString(image), movie.getSummary(), movie.getDuration());
            } else {
                return new MovieAndImageNameDTO(movie.getId(), movie.getTitle(), "", "", "", movie.getSummary(), movie.getDuration());
            }
        } catch (IOException ioException) {
            return new MovieAndImageNameDTO(movie.getId(), movie.getTitle(), "", "", "", movie.getSummary(), movie.getDuration());
        }
    }
}
