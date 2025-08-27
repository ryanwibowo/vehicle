package org.infomedia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationSearchRequest {
    private String brand;
    private String model;
    private String engine;
    private Integer yearStart;
    private Integer yearEnd;
    private Double distanceStart;
    private Double distanceEnd;
}
