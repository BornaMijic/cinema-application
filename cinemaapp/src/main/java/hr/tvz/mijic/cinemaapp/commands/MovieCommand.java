package hr.tvz.mijic.cinemaapp.commands;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;

@Data
public class MovieCommand {

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
    @Digits(integer = 10, fraction = 0)
    private int duration;

    private MultipartFile image;

    public MovieCommand(String title, String summary, int duration) {
        this.title = title;
        this.summary = summary;
        this.duration = duration;
    }
}
