package hu.cubix.timeoff.dto;

import hu.cubix.timeoff.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeoffEvaluationDto {

    private Long id;
    private RequestStatus requestStatus;
}
