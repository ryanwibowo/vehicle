package org.infomedia.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.infomedia.dto.SuggestionRequest;
import org.infomedia.dto.SuggestionResponse;
import org.infomedia.service.SuggestionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/suggestions")
@RequiredArgsConstructor
public class SuggestionController {
    private final SuggestionService suggestionService;

    /**
     * Suggest operations based on given criteria.
     *
     * @param request  the suggestion request payload
     * @param pageable the pagination and sorting information
     * @return a paginated list of {@link SuggestionResponse}
     */
    @PostMapping
    public ResponseEntity<Page<SuggestionResponse>> suggest(@Valid @RequestBody SuggestionRequest request,
                                                            Pageable pageable) {
        Page<SuggestionResponse> result = suggestionService.suggest(request, pageable);
        return ResponseEntity.ok(result);
    }
}
