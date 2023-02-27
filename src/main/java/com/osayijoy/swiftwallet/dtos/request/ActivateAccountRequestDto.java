package com.zurum.lanefinance.dtos.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ActivateAccountRequestDto {
    @Size(message = "Pin should be four digits", min = 4, max = 4)
    private String pin;

}
