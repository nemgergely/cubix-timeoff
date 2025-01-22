package hu.cubix.timeoff.mapper;

import hu.cubix.timeoff.dto.TimeoffRequestDto;
import hu.cubix.timeoff.model.TimeoffRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ITimeoffRequestMapper {

    TimeoffRequest dtoToTimeoffRequest(TimeoffRequestDto timeoffRequestDto);
    TimeoffRequestDto timeoffRequestToDto(TimeoffRequest timeoffRequest);
    List<TimeoffRequest> dtosToTimeoffRequests(List<TimeoffRequestDto> timeoffRequestDtos);
    List<TimeoffRequestDto> timeoffRequestsToDtos(List<TimeoffRequest> timeoffRequests);
}
