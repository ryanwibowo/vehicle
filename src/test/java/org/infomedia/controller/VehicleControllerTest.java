package org.infomedia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.infomedia.model.Vehicle;
import org.infomedia.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VehicleRepository vehicleRepository;

    @Test
    void createVehicle_shouldReturn201_whenValidRequest() throws Exception {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setBrand("Toyota");
        vehicle.setModel("Corolla");
        vehicle.setEngine("1.8L");
        vehicle.setMakeYear(2020);

        Mockito.when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicle)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Corolla"));
    }

    @Test
    void listVehicles_shouldReturn200_withResults() throws Exception {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setBrand("Honda");
        vehicle.setModel("Civic");

        Page<Vehicle> page =
                new PageImpl<>(List.of(vehicle), PageRequest.of(0, 10), 1);

        Mockito.when(vehicleRepository.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/vehicles?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].brand").value("Honda"))
                .andExpect(jsonPath("$.content[0].model").value("Civic"));
    }

    @Test
    void listVehicles_shouldReturn200_withEmptyResults() throws Exception {
        Page<Vehicle> emptyPage =
                new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        Mockito.when(vehicleRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/vehicles?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void createVehicle_shouldReturn400_whenInvalidRequest() throws Exception {
        Vehicle invalidVehicle = new Vehicle();

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidVehicle)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listVehicles_shouldReturn500_whenRepositoryThrowsException() throws Exception {
        Mockito.when(vehicleRepository.findAll(any(PageRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/vehicles?page=0&size=10"))
                .andExpect(status().isInternalServerError());
    }
}
