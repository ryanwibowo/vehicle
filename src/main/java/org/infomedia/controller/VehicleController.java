package org.infomedia.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.infomedia.model.Vehicle;
import org.infomedia.repository.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleRepository vehicleRepository;

    /**
     * Create a new vehicle.
     *
     * @param vehicle the vehicle payload to create
     * @return the created {@link Vehicle}
     */
    @PostMapping
    public ResponseEntity<Vehicle> create(@Valid @RequestBody Vehicle vehicle) {
        Vehicle result = vehicleRepository.save(vehicle);
        return ResponseEntity.ok(result);
    }

    /**
     * List all vehicles with pagination.
     *
     * @param pageable the pagination and sorting information
     * @return a paginated list of {@link Vehicle}
     */
    @GetMapping
    public ResponseEntity<Page<Vehicle>> list(Pageable pageable) {
        Page<Vehicle> result = vehicleRepository.findAll(pageable);
        return ResponseEntity.ok(result);
    }
}
