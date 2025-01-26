package hu.cubix.timeoff.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeoffCriteriaDto {

    private String requestStatus;
    private String namePrefix;
    private LocalDate createdFrom;
    private LocalDate createdTo;
    private LocalDate activeFrom;
    private LocalDate activeTo;
}
