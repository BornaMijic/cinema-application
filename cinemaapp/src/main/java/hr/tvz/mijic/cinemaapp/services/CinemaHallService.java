package hr.tvz.mijic.cinemaapp.services;

import hr.tvz.mijic.cinemaapp.DTOs.MultipleCinemaHallsDTO;
import hr.tvz.mijic.cinemaapp.commands.CinemaHallCommand;

import java.util.List;
import java.util.Map;

public interface CinemaHallService {
    List<MultipleCinemaHallsDTO> getAllCinemaHalls();

    Map<String, Object> findTenCinemaHallsPerPageWhichContainsSearchText(int pageNumber, String searchText);

    Map<String, Object> findTenCinemaHallsPerPage(int pageNumber);

    boolean addCinemaHall(CinemaHallCommand cinemaHallCommand);

    void deleteCinemaHallById(Long id);
}
