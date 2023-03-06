package hr.tvz.mijic.cinemaapp.DTOs;


public class RegistrationResponseAndVerificationDTO {
    private RegistrationResponseDTO registrationResponseDTO;
    private String codeToVerifyEmail;

    public RegistrationResponseAndVerificationDTO(RegistrationResponseDTO registrationResponseDTO, String codeToVerifyEmail) {
        this.registrationResponseDTO = registrationResponseDTO;
        this.codeToVerifyEmail = codeToVerifyEmail;
    }

    public RegistrationResponseDTO getRegistrationResponse() {
        return registrationResponseDTO;
    }

    public void setRegistrationResponse(RegistrationResponseDTO registrationResponseDTO) {
        this.registrationResponseDTO = registrationResponseDTO;
    }

    public String getCodeToVerifyEmail() {
        return codeToVerifyEmail;
    }

    public void setCodeToVerifyEmail(String codeToVerifyEmail) {
        this.codeToVerifyEmail = codeToVerifyEmail;
    }
}
