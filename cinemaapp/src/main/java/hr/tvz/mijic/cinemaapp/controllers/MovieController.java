package hr.tvz.mijic.cinemaapp.controllers;

import hr.tvz.mijic.cinemaapp.DTOs.*;
import hr.tvz.mijic.cinemaapp.commands.MovieCommand;
import hr.tvz.mijic.cinemaapp.commands.MovieUpdateCommand;
import hr.tvz.mijic.cinemaapp.services.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("movies")
@CrossOrigin(origins = "http://localhost:4200")
@Validated
public class MovieController {

    private MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/active")
    public List<MovieAndroidDTO> getActiveMovies() {
        return movieService.getActiveMovies();
    }

    @GetMapping("/latest")
    public List<MovieDTO> getLatestMovies() {
        return movieService.getLatestMovies();
    }

    @GetMapping("/soon-starting")
    public List<MovieDTO> getSoonStartingMovies() {
        return movieService.getSoonStartingMovies();
    }

    @GetMapping("/page/{pageNumber}")
    public ResponseEntity<PageMovieDTO> findFiveMoviesPerPage(@PathVariable int pageNumber) {
        PageMovieDTO pageMovieDTO = movieService.findFiveMoviesPerPage(pageNumber);
        return new ResponseEntity<>(pageMovieDTO, HttpStatus.OK);
    }

    @GetMapping("/page/{pageNumber}/{searchText}")
    public ResponseEntity<PageMovieDTO> findFiveMoviesPerPageWhichContainsSearchText(@PathVariable int pageNumber, @PathVariable String searchText) {
        PageMovieDTO pageMovieDTO = movieService.findFiveMoviesPerPageWhichContainsSearchText(pageNumber, searchText);
        return new ResponseEntity<>(pageMovieDTO, HttpStatus.OK);
    }

    @GetMapping("/admin/page/{pageNumber}")
    public ResponseEntity<PageMovieAdminDTO> findFiveMoviesPerPageAdmin(@PathVariable int pageNumber) {
        PageMovieAdminDTO pageMovieAdminDTO = movieService.findFiveMoviesPerPageAdmin(pageNumber);
        return new ResponseEntity<>(pageMovieAdminDTO, HttpStatus.OK);
    }

    @GetMapping("/admin/page/{pageNumber}/{searchText}")
    public ResponseEntity<PageMovieAdminDTO> findFiveMoviesPerPageWhichContainsSearchTextAdmin(@PathVariable int pageNumber, @PathVariable String searchText) {
        PageMovieAdminDTO pageMovieAdminDTO = movieService.findFiveMoviesPerPageWhichContainsSearchTextAdmin(pageNumber, searchText);
        return new ResponseEntity<>(pageMovieAdminDTO, HttpStatus.OK);
    }

    @GetMapping("/specificMovie/{id}")
    public ResponseEntity<MovieAndImageNameDTO> getMovieById(@PathVariable String id) {
        MovieAndImageNameDTO movieAndImageNameDTO = movieService.getMovieById(Long.valueOf(id));
        if (movieAndImageNameDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return new ResponseEntity<>(movieAndImageNameDTO, HttpStatus.OK);
    }

    @GetMapping("/admin/specificMovie/{id}")
    public ResponseEntity<MovieAndImageNameDTO> getMovieByIdAdmin(@PathVariable String id) {
        MovieAndImageNameDTO movieAndImageNameDTO = movieService.getMovieByIdAdmin(Long.valueOf(id));
        if (movieAndImageNameDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return new ResponseEntity<>(movieAndImageNameDTO, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<HashMap<String, Object>> addMovie(@RequestParam("title") String title, @RequestParam("summary") String summary, @RequestParam("duration") int duration, @RequestParam("image") MultipartFile image) {
        if (!(image.getOriginalFilename().endsWith(".jpg") || image.getOriginalFilename().endsWith(".png"))) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
        MovieCommand movieCommand = new MovieCommand(title, summary, duration);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator movieCommandValidator = factory.getValidator();
        Set<ConstraintViolation<MovieCommand>> errorCount = movieCommandValidator.validate(movieCommand);
        if (errorCount.size() == 0) {
            HashMap<String, Object> movieHashMap = movieService.addMovie(movieCommand, image);
            if (movieHashMap == null) {
                return new ResponseEntity(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(movieHashMap, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PutMapping(value = "update/image")
    public ResponseEntity<Void> updateMovieAndImage(@RequestParam("id") Long id, @RequestParam("title") String title, @RequestParam("summary") String summary, @RequestParam("duration") int duration, @RequestParam("oldImage") String oldImageName, @RequestParam("image") MultipartFile image) {
        if (!(image.getOriginalFilename().endsWith(".jpg") || image.getOriginalFilename().endsWith(".png"))) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
        MovieUpdateCommand movieUpdateCommand = new MovieUpdateCommand(id, title, summary, duration);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator movieCommandValidator = factory.getValidator();
        Set<ConstraintViolation<MovieUpdateCommand>> errorCount = movieCommandValidator.validate(movieUpdateCommand);
        if (errorCount.size() == 0) {
            boolean success = movieService.updateMovie(movieUpdateCommand, oldImageName, image);
            if (success == false) {
                return new ResponseEntity(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PutMapping(value = "update")
    public ResponseEntity<MovieDTO> updateMovie(@Valid @RequestBody MovieUpdateCommand movieUpdateCommand, @RequestParam("oldImage") String oldImage) {
        boolean success = movieService.updateMovie(movieUpdateCommand, oldImage, null);
        if (success == false) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MovieDTO> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(Long.valueOf(id));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}/{imageName}")
    public ResponseEntity<MovieDTO> deleteMovieAndImage(@PathVariable Long id, @PathVariable String imageName) throws IOException {
        movieService.deleteMovieAndImage(Long.valueOf(id), imageName);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
