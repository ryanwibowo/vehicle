package org.infomedia.service;

import jakarta.persistence.criteria.*;
import org.infomedia.dto.OperationRequest;
import org.infomedia.dto.OperationResponse;
import org.infomedia.dto.OperationSearchRequest;
import org.infomedia.exception.ResourceNotFoundException;
import org.infomedia.model.Operation;
import org.infomedia.repository.OperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OperationServiceTest {

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CriteriaBuilder cb;

    @InjectMocks
    private OperationService operationService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_shouldReturnOperationResponse_whenValidRequest() {
        OperationRequest request = new OperationRequest("Toyota", "Corolla", "2.0",
                2020, 2025, 60000d, 70000d, "Service",
                1500d, "Yearly Service", 5);

        Operation entity = new Operation();
        entity.setId(1L);
        entity.setBrand("Toyota");
        entity.setModel("Corolla");

        when(operationRepository.save(any(Operation.class))).thenReturn(entity);

        OperationResponse response = operationService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getBrand()).isEqualTo("Toyota");
    }

    @Test
    void create_shouldSaveAndReturnResponse() {
        OperationRequest request = new OperationRequest();
        request.setBrand("Toyota");

        Operation entity = new Operation();
        entity.setId(1L);
        entity.setBrand("Toyota");

        when(operationRepository.save(any(Operation.class))).thenReturn(entity);

        OperationResponse response = operationService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getBrand()).isEqualTo("Toyota");
        verify(operationRepository, times(1)).save(any(Operation.class));
    }


    @Test
    void update_shouldReturnUpdatedResponse_whenOperationExists() {
        OperationRequest request = new OperationRequest("Toyota", "Corolla", "2.0",
                2020, 2025, 60000d, 70000d, "Service",
                1500d, "Yearly Service", 5);

        Operation entity = new Operation();
        entity.setId(1L);
        entity.setBrand("Toyota");

        when(operationRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(operationRepository.save(entity)).thenReturn(entity);

        OperationResponse response = operationService.update(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    void update_shouldUpdateExistingOperation() {
        Long id = 1L;
        OperationRequest request = new OperationRequest();
        request.setBrand("Honda");

        Operation entity = new Operation();
        entity.setId(id);
        entity.setBrand("Toyota");

        when(operationRepository.findById(id)).thenReturn(Optional.of(entity));
        when(operationRepository.save(any(Operation.class))).thenReturn(entity);

        OperationResponse response = operationService.update(id, request);

        assertThat(response).isNotNull();
        verify(modelMapper, times(1)).map(request, entity);
        verify(operationRepository, times(1)).save(entity);
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        Long id = 99L;
        OperationRequest request = new OperationRequest();

        when(operationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> operationService.update(id, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Operation 99 not found");

        verify(operationRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowResourceNotFound_whenOperationDoesNotExist() {
        OperationRequest request = new OperationRequest("Toyota", "Corolla", "2.0",
                2020, 2025, 60000d, 70000d, "Service",
                1500d, "Yearly Service", 5);

        when(operationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> operationService.update(99L, request));
    }

    @SuppressWarnings("unchecked")
    @Test
    void search_shouldExecuteSpecificationAndReturnResults() {
        OperationSearchRequest request = new OperationSearchRequest(
                "Toyota", "Corolla", "2.0 Turbo",
                2020, 2022, 50000d, 55000d);

        Operation entity = new Operation();
        entity.setId(1L);
        entity.setBrand("Toyota");
        entity.setModel("Corolla");
        entity.setEngine("2.0 Turbo");
        entity.setYearStart(2020);
        entity.setYearEnd(2022);
        entity.setDistanceStart(50000d);
        entity.setDistanceEnd(55000d);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Operation> page = new PageImpl<>(List.of(entity));

        when(operationRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<OperationResponse> result = operationService.search(request, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent().get(0).getBrand()).isEqualTo("Toyota");
        assertThat(result.getContent().get(0).getModel()).isEqualTo("Corolla");

        ArgumentCaptor<Specification<Operation>> captor = ArgumentCaptor.forClass(Specification.class);
        verify(operationRepository).findAll(captor.capture(), eq(pageable));
        var spec = captor.getValue();

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        Root<Operation> root = mock(Root.class);
        Predicate base = mock(Predicate.class);
        Predicate newPredicate = mock(Predicate.class);

        Path<Object> brandPath = mock(Path.class);
        Expression<String> lowerExpr = mock(Expression.class);

        when(root.get("brand")).thenReturn(brandPath);
        when(cb.lower(any(Expression.class))).thenReturn(lowerExpr);
        when(cb.equal(eq(lowerExpr), any())).thenReturn(newPredicate);

        when(cb.conjunction()).thenReturn(base);
        when(cb.and(any(), any())).thenReturn(newPredicate);

        Predicate resultPredicate = spec.toPredicate(root, query, cb);
        assertThat(resultPredicate).isNotNull();
    }

    @Test
    void search_shouldReturnEmptyPage_whenNoResults() {
        OperationSearchRequest request = new OperationSearchRequest();
        request.setBrand("Unknown");

        Page<Operation> emptyPage = Page.empty();
        when(operationRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

        Page<OperationResponse> result = operationService.search(request, PageRequest.of(0, 10));

        assertThat(result).isEmpty();
    }

    @Test
    void search_shouldReturnFilteredResults() {
        OperationSearchRequest request = new OperationSearchRequest();
        request.setBrand("Toyota");

        Operation entity = new Operation();
        entity.setId(1L);
        entity.setBrand("Toyota");

        Page<Operation> page =
                new PageImpl<>(java.util.List.of(entity), PageRequest.of(0, 10), 1);

        when(operationRepository.findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        Page<OperationResponse> result = operationService.search(request, PageRequest.of(0, 10));

        assertThat(result).isNotEmpty();
        assertThat(result.getContent().get(0).getBrand()).isEqualTo("Toyota");
    }

    @Test
    void search_shouldReturnEmptyPageWhenNoMatches() {
        OperationSearchRequest request = new OperationSearchRequest();
        request.setBrand("Ford");

        when(operationRepository.findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        Page<OperationResponse> result = operationService.search(request, PageRequest.of(0, 10));

        assertThat(result).isEmpty();
    }
}
