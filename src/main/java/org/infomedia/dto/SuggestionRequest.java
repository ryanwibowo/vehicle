package org.infomedia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuggestionRequest {
    @NotBlank String brand;
    @NotBlank String model;
    @NotBlank String engine;
    @NotNull Integer makeYear;
    @NotNull Double totalDistance;
}
