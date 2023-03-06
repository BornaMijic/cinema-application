package hr.tvz.mijic.cinemaapp.services;

import hr.tvz.mijic.cinemaapp.DTOs.MultipleCinemaHallsDTO;
import hr.tvz.mijic.cinemaapp.commands.CinemaHallCommand;
import hr.tvz.mijic.cinemaapp.entities.CinemaHall;
import hr.tvz.mijic.cinemaapp.entities.Seat;
import hr.tvz.mijic.cinemaapp.repositories.CinemaHallRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CinemaHallServiceImpl implements CinemaHallService {

    private CinemaHallRepository cinemaHallRepository;

    public CinemaHallServiceImpl(CinemaHallRepository cinemaHallRepository) {
        this.cinemaHallRepository = cinemaHallRepository;
    }

    @Override
    public List<MultipleCinemaHallsDTO> getAllCinemaHalls() {
        List<CinemaHall> cinemaHallList = this.cinemaHallRepository.findAll();
        List<MultipleCinemaHallsDTO> multipleCinemaHallsDTOList = new ArrayList<>();
        for (CinemaHall cinemaHall : cinemaHallList) {
            multipleCinemaHallsDTOList.add(new MultipleCinemaHallsDTO(cinemaHall.getId(), cinemaHall.getName()));
        }
        return multipleCinemaHallsDTOList;
    }

    @Override
    public Map<String, Object> findTenCinemaHallsPerPageWhichContainsSearchText(int pageNumber, String searchText) {
        Pageable pageWithFiveMovies = PageRequest.of(pageNumber, 10);
        Page<CinemaHall> cinemaHallsPage = cinemaHallRepository.findTenCinemaHallsPerPageWhichContainsSearchText(searchText.toLowerCase(), pageWithFiveMovies);
        return getCinemaHallsMap(cinemaHallsPage);
    }

    @Override
    public Map<String, Object> findTenCinemaHallsPerPage(int pageNumber) {
        Pageable requestingPageWithFiveMovies = PageRequest.of(pageNumber, 10);
        Page<CinemaHall> cinemaHallsPage = cinemaHallRepository.findTenCinemaHallsPerPage(requestingPageWithFiveMovies);
        return getCinemaHallsMap(cinemaHallsPage);
    }


    @Override
    @Transactional
    public boolean addCinemaHall(CinemaHallCommand cinemaHallCommand) {
        CinemaHall cinemaHall = new CinemaHall(cinemaHallCommand.getName(), cinemaHallCommand.getRows(), cinemaHallCommand.getColumns());
        List<Seat> seats = cinemaHallCommand.getSeats().stream().map(seatCommand -> {
            Seat seat = new Seat(seatCommand.getSeatNumber(), seatCommand.getRowNumber(), seatCommand.getGridPosition());
            seat.setCinemaHall(cinemaHall);
            return seat;
        }).collect(Collectors.toList());
        cinemaHall.setSeats(seats);
        CinemaHall cinemaHallSave = this.cinemaHallRepository.save(cinemaHall);
        if (cinemaHallSave != null) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteCinemaHallById(Long id) {
        this.cinemaHallRepository.deleteById(id);
    }

    private Map<String, Object> getCinemaHallsMap(Page<CinemaHall> cinemaHallsPage) {
        List<MultipleCinemaHallsDTO> cinemaHallList = cinemaHallsPage.stream().map(cinemaHall -> new MultipleCinemaHallsDTO(cinemaHall.getId(), cinemaHall.getName())).collect(Collectors.toList());
        Map<String, Object> cinemaHallsMap = new HashMap<>();
        cinemaHallsMap.put("count", cinemaHallsPage.getTotalElements());
        cinemaHallsMap.put("cinemaHalls", cinemaHallList);
        return cinemaHallsMap;
    }
}
