package hr.tvz.mijic.cinemaapp.commands;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class MovieUpdateCommand {
    @NotNull
    private Long id;

    @NotBlank
    @Size(max=100)
    private String title;

    @NotBlank
    @Size(max=3000)
    private String summary;

    @NotNull
    @PositiveOrZero
    @Min(value = 0)
    @Max(value = 10000)
    private int duration;

    public MovieUpdateCommand(Long id, String title, String summary, int duration) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.duration = duration;
    }
}



