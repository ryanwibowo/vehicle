package org.infomedia.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.infomedia.dto.OperationRequest;
import org.infomedia.dto.OperationResponse;
import org.infomedia.dto.OperationSearchRequest;
import org.infomedia.exception.ResourceNotFoundException;
import org.infomedia.model.Operation;
import org.infomedia.repository.OperationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final ModelMapper modelMapper;

    private final OperationRepository operationRepository;

    /**
     * Create a new operation.
     *
     * @param request the created {@link OperationRequest} payload
     * @return the created {@link OperationResponse}
     */
    @Transactional
    public OperationResponse create(OperationRequest request) {
        Operation operation = OperationRequest.toEntity(request);
        Operation saved = operationRepository.save(operation);
        return OperationResponse.toResponse(saved);
    }

    /**
     * Update an existing operation.
     *
     * @param id the ID of the operation
     * @param request the updated {@link OperationRequest} payload
     * @return the updated {@link OperationResponse}
     * @throws ResourceNotFoundException if the operation is not found
     */
    @Transactional
    public OperationResponse update(Long id, OperationRequest request) {
        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operation %d not found".formatted(id)));
        modelMapper.map(request, operation);
        operationRepository.save(operation);
        return OperationResponse.toResponse(operation);
    }

    /**
     * Search for operations based on filter criteria.
     *
     * @param request the search filter request
     * @param pageable pagination and sorting information
     * @return a paginated list of {@link OperationResponse}
     */
    @Transactional(readOnly = true)
    public Page<OperationResponse> search(OperationSearchRequest request, Pageable pageable) {
        Specification<Operation> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            predicate = addIfNotNull(cb, predicate, request.getBrand(),
                    brand -> cb.equal(cb.lower(root.get("brand")), brand.toLowerCase()));

            predicate = addIfNotNull(cb, predicate, request.getModel(),
                    model -> cb.equal(cb.lower(root.get("model")), model.toLowerCase()));

            predicate = addIfNotNull(cb, predicate, request.getEngine(),
                    engine -> cb.equal(cb.lower(root.get("engine")), engine.toLowerCase()));

            predicate = addIfNotNull(cb, predicate, request.getYearEnd(),
                    yearEnd -> cb.greaterThanOrEqualTo(root.get("yearEnd"), yearEnd));

            predicate = addIfNotNull(cb, predicate, request.getYearStart(),
                    yearStart -> cb.lessThanOrEqualTo(root.get("yearStart"), yearStart));

            predicate = addIfNotNull(cb, predicate, request.getDistanceStart(),
                    distanceStart -> cb.greaterThanOrEqualTo(root.get("distanceEnd"), distanceStart));

            predicate = addIfNotNull(cb, predicate, request.getDistanceEnd(),
                    distanceEnd -> cb.lessThanOrEqualTo(root.get("distanceStart"), distanceEnd));

            return predicate;
        };

        return operationRepository.findAll(spec, pageable)
                .map(OperationResponse::toResponse);
    }

    private <T> Predicate addIfNotNull(
            CriteriaBuilder cb,
            Predicate base,
            T value,
            Function<T, Predicate> predicateSupplier) {

        return value != null ? cb.and(base, predicateSupplier.apply(value)) : base;
    }
}
