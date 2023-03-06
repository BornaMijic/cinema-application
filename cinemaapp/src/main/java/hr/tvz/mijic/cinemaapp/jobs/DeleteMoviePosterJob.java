package hr.tvz.mijic.cinemaapp.jobs;


import hr.tvz.mijic.cinemaapp.repositories.MovieRepository;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DeleteMoviePosterJob implements Job {
    private final MovieRepository movieRepository;

    public DeleteMoviePosterJob(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        File moviesFolder = new File("src/main/resources/movies/");

        List<String> posterNames = new ArrayList<>();

        for (File file : moviesFolder.listFiles()) {
            if (file.isFile()) {
                posterNames.add(file.getName());
            }
        }

        List<String> moviesList =
                this.movieRepository.findAll().stream()
                        .map(movie -> movie.getId() + "-" + movie.getPosterName()).toList();

        List<String> fileNames = posterNames.stream()
                .filter(fileName -> !moviesList.contains(fileName)).toList();

        for (String fileName : fileNames) {
            try {
                File imageFile = new File("src/main/resources/movies/" + fileName);
                FileUtils.forceDelete(imageFile);
                throw new IOException();
            } catch (IOException ignored) {

            }
        }
    }
}
