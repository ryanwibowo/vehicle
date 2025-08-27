package org.infomedia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.infomedia.dto.SuggestionRequest;
import org.infomedia.dto.SuggestionResponse;
import org.infomedia.service.SuggestionService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SuggestionController.class)
class SuggestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SuggestionService suggestionService;

    @Test
    void suggest_shouldReturn200_withResults() throws Exception {
        SuggestionRequest request = new SuggestionRequest("Honda", "Civic", "2.0 Turbo",
                2020, 50000d);
        SuggestionResponse response = new SuggestionResponse(1L, "Brake Pad Replacement",
                "Replace front and rear brake pads", 150d, 2,
                "Honda", "Civic", "2.0 Turbo");
        Page<SuggestionResponse> page =
                new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        Mockito.when(suggestionService.suggest(any(SuggestionRequest.class), any()))
                .thenReturn(page);

        mockMvc.perform(post("/suggestions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].operationId").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Brake Pad Replacement"));
    }

    @Test
    void suggest_shouldReturn200_withEmptyResults() throws Exception {
        SuggestionRequest request = new SuggestionRequest("Honda", "Civic", "2.0 Turbo",
                2020, 50000d);
        Page<SuggestionResponse> emptyPage =
                new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        Mockito.when(suggestionService.suggest(any(SuggestionRequest.class), any()))
                .thenReturn(emptyPage);

        mockMvc.perform(post("/suggestions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void suggest_shouldReturn400_whenInvalidRequest() throws Exception {
        SuggestionRequest invalidRequest = new SuggestionRequest();

        mockMvc.perform(post("/suggestions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void suggest_shouldReturn500_whenServiceThrowsException() throws Exception {
        SuggestionRequest request = new SuggestionRequest("Honda", "Civic", "2.0 Turbo",
                2020, 50000d);

        Mockito.when(suggestionService.suggest(any(SuggestionRequest.class), any()))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(post("/suggestions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}
