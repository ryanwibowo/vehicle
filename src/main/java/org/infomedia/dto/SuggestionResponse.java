package org.infomedia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionResponse {
    Long operationId;
    String name;
    String description;
    Double approxCost;
    Integer time;
    String brand;
    String model;
    String engine;
}
