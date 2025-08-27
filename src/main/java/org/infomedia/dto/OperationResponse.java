package org.infomedia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.infomedia.model.Operation;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationResponse {
    private Long id;
    private String brand;
    private String model;
    private String engine;
    private Integer yearStart;
    private Integer yearEnd;
    private Double distanceStart;
    private Double distanceEnd;
    private String name;
    private Double approxCost;
    private String description;
    private Integer time;

    public static OperationResponse toResponse(Operation operation) {
        return new OperationResponse(
                operation.getId(),
                operation.getBrand(),
                operation.getModel(),
                operation.getEngine(),
                operation.getYearStart(),
                operation.getYearEnd(),
                operation.getDistanceStart(),
                operation.getDistanceEnd(),
                operation.getName(),
                operation.getApproxCost(),
                operation.getDescription(),
                operation.getTime()
        );
    }
}
