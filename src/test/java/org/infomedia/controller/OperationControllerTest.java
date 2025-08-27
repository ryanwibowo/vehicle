package org.infomedia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.infomedia.dto.OperationRequest;
import org.infomedia.dto.OperationResponse;
import org.infomedia.service.OperationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OperationController.class)
class OperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OperationService operationService;

    @Test
    void create_shouldReturn200_whenValidRequest() throws Exception {
        OperationRequest request = new OperationRequest("Toyota", "Corolla", "2.0",
                2020, 2025, 60000d, 70000d, "Service",
                1500d, "Yearly Service", 5);
        OperationResponse response = new OperationResponse(1L, "Toyota", "Corolla", "2.0",
                2020, 2025, 60000d, 70000d, "Service",
                1500d, "Yearly Service", 5);

        Mockito.when(operationService.create(any(OperationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Corolla"));
    }

    @Test
    void update_shouldReturn200_whenValidIdAndRequest() throws Exception {
        Long existingId = 1L;
        OperationRequest request = new OperationRequest("Toyota", "Corolla", "2.0",
                2020, 2025, 60000d, 70000d, "Service",
                1500d, "Yearly Service", 5);
        OperationResponse response = new OperationResponse(1L, "Toyota", "Corolla", "2.0",
                2020, 2025, 60000d, 70000d, "Service",
                1500d, "Yearly Service", 5);

        Mockito.when(operationService.update(eq(existingId), any(OperationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/operations/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Corolla"));
    }

    @Test
    void search_shouldReturnPage_whenResultsExist() throws Exception {
        OperationResponse response = new OperationResponse();
        response.setId(1L);
        response.setBrand("Mazda");
        response.setModel("3");
        Page<OperationResponse> page =
                new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        Mockito.when(operationService.search(any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/operations/search")
                        .param("brand", "Mazda")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].brand").value("Mazda"))
                .andExpect(jsonPath("$.content[0].model").value("3"));
    }

    @Test
    void create_shouldReturnBadRequest_whenPayloadInvalid() throws Exception {
        OperationRequest invalidRequest = new OperationRequest();

        mockMvc.perform(post("/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldReturnNotFound_whenIdDoesNotExist() throws Exception {
        Long nonExistingId = 999L;
        OperationRequest validRequest = new OperationRequest("Toyota", "Corolla", "2.0",
                2020, 2025, 60000d, 70000d, "Service",
                1500d, "Yearly Service", 5);

        Mockito.when(operationService.update(eq(nonExistingId), any(OperationRequest.class)))
                .thenThrow(new RuntimeException("Operation not found"));

        mockMvc.perform(put("/operations/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void search_shouldReturnEmptyPage_whenNoResultsFound() throws Exception {
        Mockito.when(operationService.search(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/operations/search")
                        .param("brand", "NonExistingBrand")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }
}
