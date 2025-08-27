package org.infomedia.service;

import jakarta.persistence.criteria.*;
import org.infomedia.dto.SuggestionRequest;
import org.infomedia.dto.SuggestionResponse;
import org.infomedia.model.Operation;
import org.infomedia.repository.OperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SuggestionServiceTest {

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private org.modelmapper.ModelMapper modelMapper;

    @InjectMocks
    private SuggestionService suggestionService;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pageable = PageRequest.of(0, 10);
    }

    // ---------------- POSITIVE SCENARIOS ----------------

    @Test
    void testSuggestWithAllFilters() {
        SuggestionRequest request = new SuggestionRequest();
        request.setBrand("Toyota");
        request.setModel("Corolla");
        request.setEngine("VVTi");
        request.setMakeYear(2015);
        request.setTotalDistance(50000d);

        Operation op = new Operation();
        op.setId(1L);
        op.setBrand("Toyota");
        op.setModel("Corolla");
        op.setEngine("VVTi");

        Page<Operation> repoPage = new PageImpl<>(List.of(op), pageable, 1);

        when(operationRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(repoPage);
        when(modelMapper.map(any(Operation.class), eq(SuggestionResponse.class)))
                .thenAnswer(invocation -> {
                    Operation o = invocation.getArgument(0);
                    SuggestionResponse resp = new SuggestionResponse();
                    resp.setOperationId(o.getId());
                    resp.setBrand(o.getBrand());
                    resp.setModel(o.getModel());
                    return resp;
                });

        Page<SuggestionResponse> result = suggestionService.suggest(request, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getBrand()).isEqualTo("Toyota");
        verify(operationRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(modelMapper, times(1)).map(op, SuggestionResponse.class);
    }

    @Test
    void testSuggestWithPartialFilters() {
        SuggestionRequest request = new SuggestionRequest();
        request.setBrand("Honda");   // other fields null/0

        Operation op = new Operation();
        op.setId(2L);
        op.setBrand("Honda");

        Page<Operation> repoPage = new PageImpl<>(List.of(op), pageable, 1);

        when(operationRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(repoPage);
        when(modelMapper.map(any(Operation.class), eq(SuggestionResponse.class)))
                .thenAnswer(invocation -> {
                    Operation o = invocation.getArgument(0);
                    SuggestionResponse resp = new SuggestionResponse();
                    resp.setOperationId(o.getId());
                    resp.setBrand(o.getBrand());
                    return resp;
                });

        Page<SuggestionResponse> result = suggestionService.suggest(request, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getBrand()).isEqualTo("Honda");
    }

    // ---------------- NEGATIVE SCENARIOS ----------------

    @Test
    void testSuggestWithNoMatchingOperations() {
        SuggestionRequest request = new SuggestionRequest();
        request.setBrand("NonExistent");

        Page<Operation> emptyPage = Page.empty(pageable);
        when(operationRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        Page<SuggestionResponse> result = suggestionService.suggest(request, pageable);

        assertThat(result).isEmpty();
        verify(operationRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testSuggestWithNullRequestValues() {
        SuggestionRequest request = new SuggestionRequest(); // all fields null/0

        Operation op = new Operation();
        op.setId(3L);
        op.setBrand("Mazda");

        Page<Operation> repoPage = new PageImpl<>(List.of(op), pageable, 1);
        when(operationRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(repoPage);
        when(modelMapper.map(any(Operation.class), eq(SuggestionResponse.class)))
                .thenAnswer(invocation -> {
                    Operation o = invocation.getArgument(0);
                    SuggestionResponse resp = new SuggestionResponse();
                    resp.setOperationId(o.getId());
                    resp.setBrand(o.getBrand());
                    return resp;
                });

        Page<SuggestionResponse> result = suggestionService.suggest(request, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getBrand()).isEqualTo("Mazda");
    }

    // ---------------- SPECIFICATION LAMBDA EXECUTION ----------------

    @SuppressWarnings("unchecked")
    @Test
    void testSpecificationLambdaExecutes() {
        SuggestionRequest request = new SuggestionRequest("Toyota", "Corolla", "2.0 Turbo",
                2020, 50000d);

        Operation op = new Operation();
        op.setId(3L);
        op.setBrand("Mazda");

        Page<Operation> repoPage = new PageImpl<>(List.of(op));
        when(operationRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(repoPage);

        suggestionService.suggest(request, pageable);

        ArgumentCaptor<org.springframework.data.jpa.domain.Specification<Operation>> captor =
                ArgumentCaptor.forClass(org.springframework.data.jpa.domain.Specification.class);
        verify(operationRepository).findAll(captor.capture(), eq(pageable));
        var spec = captor.getValue();

        // Mock Criteria API
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        Root<Operation> root = mock(Root.class);
        Predicate pred = mock(Predicate.class);

        Path<Object> brandPath = mock(Path.class);
        Expression<String> lowerExpr = mock(Expression.class);

        when(root.get("brand")).thenReturn(brandPath);
        when(cb.lower(any(Expression.class))).thenReturn(lowerExpr);
        when(cb.equal(lowerExpr, "toyota")).thenReturn(pred);
        when(cb.and(any(Predicate[].class))).thenReturn(pred);

        Predicate resultPredicate = spec.toPredicate(root, query, cb);
        assertThat(resultPredicate).isNotNull();
    }
}
