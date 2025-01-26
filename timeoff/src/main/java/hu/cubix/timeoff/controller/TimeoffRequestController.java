package hu.cubix.timeoff.controller;

import com.fasterxml.jackson.annotation.JsonView;
import hu.cubix.timeoff.dto.TimeoffCriteriaDto;
import hu.cubix.timeoff.dto.TimeoffEvaluationDto;
import hu.cubix.timeoff.dto.TimeoffRequestDto;
import hu.cubix.timeoff.jsonviews.Views;
import hu.cubix.timeoff.mapper.ITimeoffRequestMapper;
import hu.cubix.timeoff.model.TimeoffRequest;
import hu.cubix.timeoff.service.TimeoffRequestService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/timeoff")
public class TimeoffRequestController {

    private final ITimeoffRequestMapper timeoffRequestMapper;
    private final TimeoffRequestService timeoffRequestService;

    @JsonView(Views.RequestView.class)
    @PostMapping("/filtered")
    public List<TimeoffRequestDto> findAllTimeoffRequests(
        @RequestBody TimeoffCriteriaDto timeoffCriteriaDto, @SortDefault("id") Pageable pageable) {

        Page<TimeoffRequest> timeoffRequestPage = timeoffRequestService.findTimeoffRequestsBySpecification(
            timeoffCriteriaDto, pageable
        );
        List<TimeoffRequest> timeoffRequests = timeoffRequestPage.getContent();

        return timeoffRequestMapper.timeoffRequestsToDtos(timeoffRequests);
    }

    @JsonView(Views.RequestView.class)
    @PostMapping
    public TimeoffRequestDto createTimeoffRequest(
        @RequestParam Long requesterId,
        @RequestBody TimeoffRequestDto timeoffRequestDto) {

        TimeoffRequest timeoffRequest = timeoffRequestMapper.dtoToTimeoffRequest(timeoffRequestDto);
        TimeoffRequest newTimeoffRequest = timeoffRequestService.createTimeoffRequest(requesterId, timeoffRequest);
        if (newTimeoffRequest == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return timeoffRequestMapper.timeoffRequestToDto(newTimeoffRequest);
    }

    @JsonView(Views.RequestView.class)
    @PatchMapping(value = "/evaluate")
    public TimeoffRequestDto evaluateTimeoffRequest(@RequestBody TimeoffEvaluationDto timeoffEvaluationDto) {
        TimeoffRequest evaluatedTimeoffRequest = timeoffRequestService.evaluateTimeoffRequest(
            timeoffEvaluationDto.getId(),
            timeoffEvaluationDto.getEvaluation()
        );
        return timeoffRequestMapper.timeoffRequestToDto(evaluatedTimeoffRequest);
    }

    @JsonView(Views.RequestView.class)
    @PutMapping
    public TimeoffRequestDto updateTimeoffRequest(@RequestBody TimeoffRequestDto updatedTimeoffRequestDto) {
        TimeoffRequest updatedTimeoffRequest = timeoffRequestService.updateTimeoffRequest(
            timeoffRequestMapper.dtoToTimeoffRequest(updatedTimeoffRequestDto)
        );
        return timeoffRequestMapper.timeoffRequestToDto(updatedTimeoffRequest);
    }

    @DeleteMapping(params = "id")
    public void deleteTimeoffRequest(@RequestParam Long id) {
        timeoffRequestService.deleteTimeoffRequest(id);
    }
}
