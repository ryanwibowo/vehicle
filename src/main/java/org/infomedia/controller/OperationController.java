package org.infomedia.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.infomedia.dto.OperationRequest;
import org.infomedia.dto.OperationResponse;
import org.infomedia.dto.OperationSearchRequest;
import org.infomedia.service.OperationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operations")
@RequiredArgsConstructor
public class OperationController {
    private final OperationService operationService;

    /**
     * Create a new operation.
     *
     * @param request the operation request payload
     * @return the created {@link OperationResponse}
     */
    @PostMapping
    public ResponseEntity<OperationResponse> create(@Valid @RequestBody OperationRequest request) {
        OperationResponse operationResponse = operationService.create(request);
        return ResponseEntity.ok(operationResponse);
    }

    /**
     * Update an existing operation by ID.
     *
     * @param id the ID of the operation to update
     * @param request the updated operation request payload
     * @return the updated {@link OperationResponse}
     */
    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> update(@PathVariable("id") Long id,
                                                    @Valid @RequestBody OperationRequest request) {
        OperationResponse operationResponse = operationService.update(id, request);
        return ResponseEntity.ok(operationResponse);
    }

    /**
     * Search for operations with filter and pagination.
     *
     * @param request the search filter criteria
     * @param pageable pagination and sorting info
     * @return a paginated list of {@link OperationResponse}
     */
    @GetMapping("/search")
    public ResponseEntity<Page<OperationResponse>> search(@ModelAttribute OperationSearchRequest request,
                                                          Pageable pageable) {
        Page<OperationResponse> result = operationService.search(request, pageable);
        return ResponseEntity.ok(result);
    }
}
