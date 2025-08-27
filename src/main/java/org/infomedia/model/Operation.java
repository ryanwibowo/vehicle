package org.infomedia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(indexes = {
        @Index(name = "idx_op_brand_model_engine", columnList = "brand,model,engine"),
        @Index(name = "idx_op_year_range", columnList = "yearStart,yearEnd"),
        @Index(name = "idx_op_distance_range", columnList = "distanceStart,distanceEnd")
})
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotBlank
    private String engine;

    @NotNull
    private Integer yearStart;

    @NotNull
    private Integer yearEnd;

    @NotNull
    private Double distanceStart;

    @NotNull
    private Double distanceEnd;

    @NotBlank
    private String name;

    @NotNull
    private Double approxCost;

    @Column(length = 2000)
    private String description;

    @NotNull
    private Integer time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
}
