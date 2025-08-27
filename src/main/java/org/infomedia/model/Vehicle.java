package org.infomedia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.infomedia.validation.ValidYear;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "vehicle",
        uniqueConstraints = @UniqueConstraint(columnNames = {"brand", "model", "engine", "make_year"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotBlank
    private String engine;

    @ValidYear
    @NotNull
    private Integer makeYear;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Operation> operations = new ArrayList<>();
}
