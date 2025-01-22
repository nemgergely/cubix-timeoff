package hu.cubix.timeoff.controller;

import hu.cubix.timeoff.dto.TimeoffRequestDto;
import hu.cubix.timeoff.mapper.ITimeoffRequestMapper;
import hu.cubix.timeoff.model.TimeoffRequest;
import hu.cubix.timeoff.service.TimeoffRequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/timeoff")
public class TimeoffRequestController {

    private final ITimeoffRequestMapper timeoffRequestMapper;
    private final TimeoffRequestService timeoffRequestService;

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
}
