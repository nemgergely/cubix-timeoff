package hu.cubix.timeoff.mapper;

import hu.cubix.timeoff.dto.TimeoffRequestDto;
import hu.cubix.timeoff.model.TimeoffRequest;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ITimeoffRequestMapper {

    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "approver", ignore = true)
    TimeoffRequest dtoToTimeoffRequest(TimeoffRequestDto timeoffRequestDto);

    @Mapping(target = "requester.requests", ignore = true)
    @Mapping(target = "requester.approvals", ignore = true)
    @Mapping(target = "requester.subordinates", ignore = true)
    @Mapping(target = "approver.requests", ignore = true)
    @Mapping(target = "approver.approvals", ignore = true)
    @Mapping(target = "approver.subordinates", ignore = true)
    @Named("basicInfo")
    TimeoffRequestDto timeoffRequestToDto(TimeoffRequest timeoffRequest);

    @IterableMapping(qualifiedByName = "basicInfo")
    List<TimeoffRequestDto> timeoffRequestsToDtos(List<TimeoffRequest> timeoffRequests);
}
