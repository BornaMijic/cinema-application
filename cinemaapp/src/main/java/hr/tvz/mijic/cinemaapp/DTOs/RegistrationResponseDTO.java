package hr.tvz.mijic.cinemaapp.DTOs;

public class RegistrationResponseDTO {
    private String registrationStringResponse;

    public RegistrationResponseDTO(String registrationStringResponse) {
        this.registrationStringResponse = registrationStringResponse;
    }

    public String getRegistrationStringResponse() {
        return registrationStringResponse;
    }

    public void setRegistrationStringResponse(String registrationStringResponse) {
        this.registrationStringResponse = registrationStringResponse;
    }
}
