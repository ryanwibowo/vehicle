package org.infomedia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.infomedia.model.Operation;
import org.infomedia.validation.ValidYear;
import org.infomedia.validation.ValidYearRange;

@Data
@ValidYearRange
@AllArgsConstructor
@NoArgsConstructor
public class OperationRequest {
    @NotBlank
    private String brand;
    @NotBlank
    private String model;
    @NotBlank
    private String engine;
    @NotNull
    @ValidYear
    private Integer yearStart;
    @NotNull
    private Integer yearEnd;
    private Double distanceStart;
    private Double distanceEnd;
    @NotBlank
    private String name;
    private Double approxCost;
    private String description;
    private Integer time;

    public static Operation toEntity(OperationRequest request) {
        Operation operation = new Operation();
        operation.setBrand(request.brand);
        operation.setModel(request.model);
        operation.setEngine(request.engine);
        operation.setYearStart(request.yearStart);
        operation.setYearEnd(request.yearEnd);
        operation.setDistanceStart(request.distanceStart);
        operation.setDistanceEnd(request.distanceEnd);
        operation.setName(request.name);
        operation.setApproxCost(request.approxCost);
        operation.setDescription(request.description);
        operation.setTime(request.time);
        return operation;
    }
}
