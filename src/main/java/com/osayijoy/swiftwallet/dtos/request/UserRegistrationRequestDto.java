package com.zurum.lanefinance.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zurum.lanefinance.utils.validations.PhoneNumber;
import com.zurum.lanefinance.utils.validations.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;



@Data
@Builder
@Schema
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserRegistrationRequestDto {

    @Schema(title = "firstName can only have letters")
    @NotBlank(message = "firstName cannot be empty")
    @Pattern(regexp = "[a-zA-Z]*", message = "FirstName can only have letters")
    @Size(message = "FirstName character length cannot be less than 2 and more than 100", min = 2, max = 100)
    private String firstName;

    @Schema(title = "lastName can only have letters")
    @NotBlank(message = "Lastname cannot be empty")
    @Pattern(regexp = "[a-zA-Z]*", message = "lastName can only have letters")
    @Size(message = "Lastname character length cannot be less than 2 and more than 100", min = 2, max = 100)
    private String lastName;

    @Schema(title = "Must be a valid email!")
    @NotBlank(message = "email cannot be empty")
    @Email(message = "Must be a valid email!")
    private String email;

    @ValidPassword
    @Schema(title = "Password must be greater than 6 and less than 20")
    private String password;

    //Interface Annotation and Validator to create custom Annotation
    @ValidPassword
    @Schema(title = "confirmPassword must be greater than 6 and less than 20")
    private String confirmPassword;

    @PhoneNumber
    @Schema(title = "Phone number character length cannot be less than 10 and more than 16")
    @NotBlank(message = "phoneNumber cannot be empty")
    @Size(message = "Phone number character length cannot be less than 10 and more than 14", min = 10, max = 16)
    private String phoneNumber;

    private String role;

}
