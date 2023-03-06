package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

@Data
public class NameAndSurnameDTO {
    private Long id;
    private String name;
    private String surname;

    public NameAndSurnameDTO(Long id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

}
