package org.infomedia.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.infomedia.dto.SuggestionRequest;
import org.infomedia.dto.SuggestionResponse;
import org.infomedia.model.Operation;
import org.infomedia.repository.OperationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import systems.uom.common.USCustomary;
import tech.units.indriya.quantity.Quantities;

import javax.measure.MetricPrefix;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class SuggestionService {

    private final ModelMapper modelMapper;

    private final OperationRepository operationRepository;

    /**
     * Returns a page of suggested operations based on the filter criteria.
     *
     * @param request  the suggestion filter criteria
     * @param pageable pagination and sorting information
     * @return a page of {@link SuggestionResponse} matching the filters
     */
    @Transactional(readOnly = true)
    public Page<SuggestionResponse> suggest(SuggestionRequest request, Pageable pageable) {
        Specification<Operation> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            addIfNotNull(request.getBrand(),
                    () -> cb.equal(cb.lower(root.get("brand")), request.getBrand().toLowerCase()), predicates);
            addIfNotNull(request.getModel(),
                    () -> cb.equal(cb.lower(root.get("model")), request.getModel().toLowerCase()), predicates);
            addIfNotNull(request.getEngine(),
                    () -> cb.equal(cb.lower(root.get("engine")), request.getEngine().toLowerCase()), predicates);

            if (request.getMakeYear() != 0) {
                int year = request.getMakeYear();
                predicates.add(cb.lessThanOrEqualTo(root.get("yearStart"), year));
                predicates.add(cb.greaterThanOrEqualTo(root.get("yearEnd"), year));
            }

            addIfNotNull(request.getTotalDistance(), () -> {
                Quantity<Length> miles = Quantities.getQuantity(request.getTotalDistance(), USCustomary.MILE);
                double distanceKm = miles.to(MetricPrefix.KILO(USCustomary.METER)).getValue().doubleValue();
                return cb.and(
                        cb.lessThanOrEqualTo(root.get("distanceStart"), distanceKm),
                        cb.greaterThanOrEqualTo(root.get("distanceEnd"), distanceKm)
                );
            }, predicates);

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return operationRepository.findAll(spec, pageable)
                .map(operation -> modelMapper.map(operation, SuggestionResponse.class));
    }

    private <T> void addIfNotNull(T value, Supplier<Predicate> supplier, List<Predicate> predicates) {
        if (value != null) {
            predicates.add(supplier.get());
        }
    }
}
